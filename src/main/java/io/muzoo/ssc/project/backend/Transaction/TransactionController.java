package io.muzoo.ssc.project.backend.Transaction;

import io.muzoo.ssc.project.backend.AnalyticsDTO.AmountDTO;
import io.muzoo.ssc.project.backend.AnalyticsDTO.GraphDataDTO;
import io.muzoo.ssc.project.backend.AnalyticsDTO.TagStatsDTO;
import io.muzoo.ssc.project.backend.User.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


@RestController
public class TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    public User verifyUser(Object principal) {
        if (!(principal instanceof UserDetails)) {
            throw new IllegalStateException("User not authenticated");
        }

        UserDetails userDetails = (UserDetails) principal;
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByUsername(userDetails.getUsername()));
        if (optionalUser.isEmpty()) {
            throw new IllegalStateException("User not found");
        }
        return optionalUser.get();
    }

    @PostMapping("/api/createTransactions")
    public TransactionDTO createTransaction(HttpServletRequest request) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = verifyUser(principal);

        // Extracting and parsing request parameters
        String tagIdParam = request.getParameter("tagId");
        String tagId2Param = request.getParameter("tagId2");

        long tagId = 0; // Default value
        long tagId2 = 0; // Default value

        try {
            // Only parse if the parameters are not null and not empty
            if (tagIdParam != null && !tagIdParam.isEmpty()) {
                tagId = Long.parseLong(tagIdParam);
            }
            if (tagId2Param != null && !tagId2Param.isEmpty()) {
                tagId2 = Long.parseLong(tagId2Param);
            }
        } catch (NumberFormatException e) {
            // Log error or handle the case where parameters are invalid
            System.err.println("Error parsing tagId or tagId2 from request parameters");
        }

        String type = request.getParameter("type");
        String notes = request.getParameter("notes");
        BigDecimal value = new BigDecimal(request.getParameter("value"));
        Timestamp timestamp;
        try {
            Date parsedDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(request.getParameter("timestamp"));
            timestamp = new Timestamp(parsedDate.getTime());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format", e);
        }

        Transaction transaction = new Transaction();
        transaction.setUserId(user.getId()); // Ensuring the transaction belongs to the authenticated user
        transaction.setTagId(tagId);
        transaction.setTagId2(tagId2);

        try {
            transaction.setType(Type.parseType(type));
            if (transaction.getType() == Type.NONE) {
                throw new IllegalArgumentException("Invalid transaction type");
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid transaction type", e);
        }

        transaction.setNotes(notes);
        transaction.setValue(value);
        transaction.setTimestamp(timestamp);

        transaction = transactionRepository.save(transaction);

        return TransactionDTO.builder()
                .id(transaction.getId())
                .userId(transaction.getUserId())
                .tagId(transaction.getTagId())
                .tagId2(transaction.getTagId2())
                .type(transaction.getType().name())
                .notes(transaction.getNotes())
                .value(transaction.getValue())
                .timestamp(transaction.getTimestamp())
                .build();
    }

    @GetMapping("/api/transactions/{transactionType}/{month}")
    public AmountDTO getTransaction(@PathVariable String transactionType, @PathVariable int month) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = verifyUser(principal);
        Type type = Type.parseType(transactionType);
        Double monthlyAmount = transactionRepository.sumAmountByUserIdAndMonthAndType(user.getId(), month, type);
        if (monthlyAmount == null) {
            monthlyAmount = 0.0;
        }
        return AmountDTO.builder()
                .totalAmount(new BigDecimal(monthlyAmount))
                .found(true)
                .build();
    }

    @GetMapping("/api/transactions/{transactionType}/{month}/tag-stats")
    public TagStatsDTO getTagStats(@PathVariable int month, @PathVariable String transactionType) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = verifyUser(principal);
        Type type = Type.parseType(transactionType);
        List<Object[]> tagStats = transactionRepository.sumAmountByUserIdAndMonthGroupByTag(user.getId(), month, type);
        boolean success = tagStats.isEmpty();
        return TagStatsDTO.builder()
                .tagStats(tagStats)
                .userId(user.getId())
                .empty(success)
                .build();
    }

    @GetMapping("/api/transactions/graph-data/tags")
    public GraphDataDTO getGraphDataWithTag(@RequestParam String startDate, @RequestParam String endDate,
                                            @RequestParam String transactionType, @RequestParam String dateFormat,
                                            @RequestParam String primaryTag, @RequestParam String secondaryTag) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = verifyUser(principal);
        List<Object[]> graphData = transactionRepository.getChartData(user.getId(), parseTimestamp(startDate), parseTimestamp(endDate),
                Type.parseType(transactionType), dateFormat, Long.parseLong(primaryTag), Long.parseLong(secondaryTag));
        return GraphDataDTO.builder()
                .data(graphData)
                .tagged(true)
                .empty(graphData.isEmpty())
                .build();
    }

    @GetMapping("/api/transactions/graph-data/no-tags")
    public GraphDataDTO getGraphDataWithTag(@RequestParam String startDate, @RequestParam String endDate,
                                            @RequestParam String transactionType, @RequestParam String dateFormat) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = verifyUser(principal);
        List<Object[]> graphData = transactionRepository.getChartDataNoTag(user.getId(), parseTimestamp(startDate), parseTimestamp(endDate), Type.parseType(transactionType), dateFormat);
        return GraphDataDTO.builder()
                .data(graphData)
                .tagged(false)
                .empty(graphData.isEmpty())
                .build();
    }

    private Timestamp parseTimestamp(String dateString) {
        return Timestamp.valueOf(dateString + " 00:00:00");
    }

    @GetMapping("/api/user/top-expenditures")
    public HomePageDTO getTopExpenditures() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        if (user == null) {
            return HomePageDTO.builder()
                    .success(false)
                    .message("User not found")
                    .build();
        }

        List<Object[]> rawExpenditures = transactionRepository.findTopExpendituresByUserId(user.getId());
        List<Map<String, Object>> expenditures = rawExpenditures.stream().map(result -> {
            Map<String, Object> expenditureMap = new HashMap<>();
            expenditureMap.put("tagId", ((Number) result[0]).longValue());
            expenditureMap.put("totalExpenditure", result[1]);
            expenditureMap.put("tagName", result[2]);
            return expenditureMap;
        }).collect(Collectors.toList());

        return HomePageDTO.builder()
                .success(true)
                .message("Expenditures fetched successfully")
                .data(expenditures)
                .build();
    }


    @GetMapping("/api/user/weekly-finance-summary")
    public HomePageDTO getWeeklyFinanceSummary() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        if (user == null) {
            return HomePageDTO.builder()
                    .success(false)
                    .message("User not found")
                    .build();
        }

        Timestamp oneWeekAgo = Timestamp.from(Instant.now().minus(7, ChronoUnit.DAYS));
        List<Transaction> transactions = transactionRepository.findByUserIdAndTimestampAfter(user.getId(), oneWeekAgo);

        final BigDecimal[] totalIncome = {BigDecimal.valueOf(0.0)};
        final BigDecimal[] totalExpenditure = {BigDecimal.valueOf(0.0)};

        transactions.stream()
                .map(transaction -> {
                    if ("INCOME".equals(transaction.getType().toString())) {
                        totalIncome[0] = totalIncome[0].add(transaction.getValue());
                    } else if ("EXPENDITURE".equals(transaction.getType().toString())) {
                        totalExpenditure[0] = totalExpenditure[0].add(transaction.getValue());
                    }

                    return TransactionDTO.builder()
                            .id(transaction.getId())
                            .userId(transaction.getUserId())
                            .tagId(transaction.getTagId())
                            .tagId2(transaction.getTagId2())
                            .type(transaction.getType().toString())
                            .notes(transaction.getNotes())
                            .value(transaction.getValue())
                            .timestamp(transaction.getTimestamp())
                            .build();
                })
                .collect(Collectors.toList());

        BigDecimal balance = totalIncome[0].subtract(totalExpenditure[0]);

        // Adjust your HomePageDTO to include BigDecimal for totalIncome, totalExpenditure, and balance
        return HomePageDTO.builder()
                .success(true)
                .message("Weekly finance summary fetched successfully")
                // Ensure your HomePageDTO can accept BigDecimal for these fields
                .totalIncome(totalIncome[0])
                .totalExpenditure(totalExpenditure[0])
                .balance(balance)
                .build();
    }
}
