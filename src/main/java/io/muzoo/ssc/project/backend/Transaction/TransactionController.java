package io.muzoo.ssc.project.backend.Transaction;

import io.muzoo.ssc.project.backend.User.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;

@RestController
public class TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/api/transactions")
    public TransactionDTO createTransaction(HttpServletRequest request) {
//        TimeZone timezone;
//        try {
//            timezone = RequestContextUtils.getTimeZone(request);
//            System.out.println(timezone);
//        } catch (Exception e) {
//            throw new IllegalStateException("Timezone error", e);
//        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UserDetails)) {
            throw new IllegalStateException("User not authenticated");
        }

        UserDetails userDetails = (UserDetails) principal;
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByUsername(userDetails.getUsername()));
        if (!optionalUser.isPresent()) {
            throw new IllegalStateException("User not found");
        }
        User user = optionalUser.get();

        // Extracting and parsing request parameters
        long tagId = Long.parseLong(request.getParameter("tagId"));
        long tagId2 = Long.parseLong(request.getParameter("tagId2"));
        String type = request.getParameter("type");
        String notes = request.getParameter("notes");
        BigDecimal value = new BigDecimal(request.getParameter("value"));
        Timestamp timestamp;
        try {
            Date parsedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(request.getParameter("timestamp"));
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
}
