package io.muzoo.ssc.project.backend.Transaction;

import io.muzoo.ssc.project.backend.SidebarDTO.AmountDTO;
import io.muzoo.ssc.project.backend.SidebarDTO.TagStatsDTO;
import io.muzoo.ssc.project.backend.User.*;
import io.muzoo.ssc.project.backend.userverify.UserVerifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    public User verifyUser(Object principal){
        return UserVerifier.getInstance().verifyUser(principal, userRepository);
    }

    @PostMapping("/api/createTransactions")
    public TransactionDTO createTransaction(HttpServletRequest request) {
//        TimeZone timezone;
//        try {
//            timezone = RequestContextUtils.getTimeZone(request);
//            System.out.println(timezone);
//        } catch (Exception e) {
//            throw new IllegalStateException("Timezone error", e);
//        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = verifyUser(principal);

        // Extracting and parsing request parameters
//        long tagId = Long.parseLong(request.getParameter("tagId"));
//        long tagId2 = Long.parseLong(request.getParameter("tagId2"));
        long tagId = 1L;
        long tagId2 = 2L;
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
    public AmountDTO getTransaction(@PathVariable String transactionType, @PathVariable int month){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = verifyUser(principal);
        Type type = Type.parseType(transactionType);
        Double monthlyAmount = transactionRepository.sumAmountByUserIdAndMonthAndType(user.getId(), month , type);
        if (monthlyAmount == null){
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
}
