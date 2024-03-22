package io.muzoo.ssc.project.backend.shortcuts.transactionblueprints.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import io.muzoo.ssc.project.backend.Transaction.TransactionController;
import io.muzoo.ssc.project.backend.User.User;
import io.muzoo.ssc.project.backend.shortcuts.transactionblueprints.TransactionBlueprintsDTO;
import io.muzoo.ssc.project.backend.shortcuts.transactionblueprints.service.TransactionBlueprintsService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class TransactionBlueprintsController {
    
    @Autowired
    private TransactionBlueprintsService transactionBlueprintsService;
    @Autowired
    private TransactionController transactionController;

    private User verifyUser(){
        final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return transactionController.verifyUser(principal);
    }

    @GetMapping("/api/transaction-blueprints/get-transaction-blueprints/favorites")
    public TransactionBlueprintsDTO getFavoriteTransactionBlueprintsDTO() {
        final User user = verifyUser();
        return transactionBlueprintsService.getFavoriteTransactionBlueprintsDTO(user);
    }

    @GetMapping("/api/transaction-blueprints/get-transaction-blueprints/recurring")
    public TransactionBlueprintsDTO getRecurringTransactionBlueprints(){
        final User user = verifyUser();
        return transactionBlueprintsService.getRecurringTransactionBlueprintsDTO(user);
    }

    @PostMapping("/api/transaction-blueprints/post-transaction-blueprints")
    public TransactionBlueprintsDTO postTransactionBlueprints(HttpServletRequest request){
        final User user = verifyUser();
        return transactionBlueprintsService.postTransactionBlueprintsDTO(request,user);
    }

    @PostMapping("/api/transaction-blueprints/delete-favorite")
    public TransactionBlueprintsDTO deleteFavorite(HttpServletRequest request) {
        // Verify the user
        final User user = verifyUser();

        // Parse the ID from the request
        final Long id = Long.parseLong(request.getParameter("id"));

        // Call the service layer to perform the deletion
        transactionBlueprintsService.deleteTransactionBlueprint(id, user);
        return transactionBlueprintsService.getTransactionBlueprintsDTO(user);
    }


}
