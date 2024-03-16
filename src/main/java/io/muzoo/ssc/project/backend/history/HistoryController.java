package io.muzoo.ssc.project.backend.history;

import com.google.gson.Gson;
import io.muzoo.ssc.project.backend.Transaction.Transaction;
import io.muzoo.ssc.project.backend.Transaction.TransactionRepository;
import io.muzoo.ssc.project.backend.Transaction.Type;
import io.muzoo.ssc.project.backend.User.User;
import io.muzoo.ssc.project.backend.User.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * A controller to retrieve current logged-in user.
 */
@RestController
public class HistoryController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    Gson gson = new Gson();


    /**
     * Make sure that all API path begins with /api. This ends up being useful for when we do proxy
     */
    @GetMapping("/api/history")
    public HistoryDTO history() {
        try {
            // The line below has the potential for a NullPointException due to nesting dot notation
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal != null && principal instanceof org.springframework.security.core.userdetails.User) {
                // user is logged in
                org.springframework.security.core.userdetails.User user =
                        (org.springframework.security.core.userdetails.User) principal;
                User u = userRepository.findByUsername(user.getUsername());
                HistoryDTO output = HistoryDTO.builder().build();
                output.setLoggedIn(true);
                output.setTransactions(transactionRepository.findAllByUserId(u.getId()));
                return output;
            }
        } catch (Exception e) {
            // Ajarn just left this blank lmao
        }
        // user is not logged in
        return HistoryDTO.builder()
                .loggedIn(false)
                .build();
    }

    @PostMapping("/api/history")
    public HistoryDTO history(HttpServletRequest request) {
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
        long id = Long.parseLong(request.getParameter("id"));
        EditType editType = EditType.parseType(request.getParameter("editType"));
        switch (editType) {
            case EDIT -> {
                long tagId = Long.parseLong(request.getParameter("tagId"));
                long tagId2 = Long.parseLong(request.getParameter("tagId2"));
                String type = request.getParameter("type");
                String notes = request.getParameter("notes");
                BigDecimal value = new BigDecimal(request.getParameter("value"));
                Timestamp timestamp;
                try {
                    Date parsedDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse(request.getParameter("timestamp"));
                    timestamp = new Timestamp(parsedDate.getTime());
                } catch (ParseException e) {
                    throw new IllegalArgumentException("Invalid date format", e);
                }
                Transaction transaction = transactionRepository.getReferenceById(id);
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
                transactionRepository.save(transaction);
                break;
            }
            case DELETE -> {
                transactionRepository.deleteById(id);
                break;
            }
            default -> throw new IllegalStateException("EditType invalid");
        }
        HistoryDTO output = HistoryDTO.builder().build();
        output.setLoggedIn(true);
        output.setTransactions(transactionRepository.findAllByUserId(user.getId()));
        return output;
    }
}
