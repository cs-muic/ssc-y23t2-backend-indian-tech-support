package io.muzoo.ssc.project.backend.history;

import com.google.gson.Gson;
import io.muzoo.ssc.project.backend.SimpleResponseDTO;
import io.muzoo.ssc.project.backend.Transaction.Transaction;
import io.muzoo.ssc.project.backend.Transaction.TransactionRepository;
import io.muzoo.ssc.project.backend.Transaction.Type;
import io.muzoo.ssc.project.backend.User.User;
import io.muzoo.ssc.project.backend.User.UserRepository;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

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

        String id = request.getParameter("id");
        String userId = request.getParameter("userId");
        String tagId = request.getParameter("tagId");
        String tagId2 = request.getParameter("tagId2");
        String type = request.getParameter("type");
        String notes = request.getParameter("notes");
        String timestamp = request.getParameter("timestamp");
        String editType = request.getParameter("editType");
        // TODO: Finish parsing the request params
        try {
            // The line below has the potential for a NullPointException due to nesting dot notation
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal != null && principal instanceof org.springframework.security.core.userdetails.User) {
                // Update the transaction repository based on the request details.
                if (Objects.equals(editType, "delete")) {
                    if (id != null) {
                        transactionRepository.deleteById(Long.parseLong(id));
                    }
                }
                else if (Objects.equals(editType, "edit")) {
                    if (id != null) {
                        if (transactionRepository.existsById(Long.parseLong(id))){
                            // Parse through every col value

                            Transaction transactionToUpdate = transactionRepository.findById(Long.parseLong(id));
                            if (userId != null) {
                                transactionToUpdate.setUserId(Long.parseLong(userId));
                            }
                            if (type != null) {
                                if (Type.parseType(type) != Type.NONE) {
                                    transactionToUpdate.setType(Type.parseType(type));
                                }
                            }
                            // The following can be null
                            transactionToUpdate.setTagId(Long.parseLong(tagId));
                            transactionToUpdate.setNotes(notes);
                            // TODO: Set the time stamp
//                            transactionToUpdate.setTimestamp();
                            transactionRepository.save(transactionToUpdate);
                        }
                    }
                }


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
}
