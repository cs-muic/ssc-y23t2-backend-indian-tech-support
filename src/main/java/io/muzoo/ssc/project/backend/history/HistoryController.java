package io.muzoo.ssc.project.backend.history;

import io.muzoo.ssc.project.backend.Transaction.Transaction;
import io.muzoo.ssc.project.backend.Transaction.TransactionRepository;
import io.muzoo.ssc.project.backend.User.User;
import io.muzoo.ssc.project.backend.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A controller to retrieve current logged-in user.
 */
@RestController
public class HistoryController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

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
                org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) principal;
                User u = userRepository.findByUsername(user.getUsername());
                return HistoryDTO.builder()
                        .loggedIn(true)
                        .transactions(transactionRepository.findAllByUserId(u.getId()))
                        .build();
            }
        } catch (Exception e) {
            // Ajarn just left this blank lmao
        }
        // user is not logged in
        return HistoryDTO.builder()
                .loggedIn(false)
                .build();
    }
}
