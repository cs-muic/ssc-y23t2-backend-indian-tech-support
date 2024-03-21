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

    @Deprecated
    @PostMapping("/api/transaction-blueprints/post-transaction-blueprints")
    public TransactionBlueprintsDTO postTransactionBlueprints(HttpServletRequest request){
        final User user = verifyUser();
        return transactionBlueprintsService.postTransactionBlueprintsDTO(request,user);
    }

    @PostMapping("/api/transaction-blueprints/recurring/create")
    public TransactionBlueprintsDTO createTransactionBlueprintsRecurrings(HttpServletRequest request){
        final User user = verifyUser();
        return transactionBlueprintsService.createTransactionBlueprintDTORecurrings(request,user);
    }

    @PostMapping("/api/transaction-blueprints/recurring/edit")
    public TransactionBlueprintsDTO editTransactionBlueprintsRecurrings(HttpServletRequest request){
        final User user = verifyUser();
        return transactionBlueprintsService.editTransactionBlueprintDTORecurrings(request,user);
    }

    @PostMapping("/api/transaction-blueprints/recurring/delete")
    public TransactionBlueprintsDTO deleteTransactionBlueprintsRecurrings(HttpServletRequest request){
        final User user = verifyUser();
        return transactionBlueprintsService.deleteTransactionBlueprintDTORecurrings(request,user);
    }

    @PostMapping("/api/transaction-blueprints/favorites/create")
    public TransactionBlueprintsDTO createTransactionBlueprintsFavorites(HttpServletRequest request){
        final User user = verifyUser();
        return transactionBlueprintsService.createTransactionBlueprintDTOFavorites(request,user);
    }

    @PostMapping("/api/transaction-blueprints/favorites/edit")
    public TransactionBlueprintsDTO editTransactionBlueprintsFavorites(HttpServletRequest request){
        final User user = verifyUser();
        return transactionBlueprintsService.editTransactionBlueprintDTOFavorites(request,user);
    }

    @PostMapping("/api/transaction-blueprints/favorites/delete")
    public TransactionBlueprintsDTO deleteTransactionBlueprintsDTOFavorites(HttpServletRequest request){
        final User user = verifyUser();
        return transactionBlueprintsService.deleteTransactionBlueprintDTOFavorites(request,user);
    }

    @PostMapping("/api/transaction-blueprints/delete-favorite")
    public TransactionBlueprintsDTO deleteFavorite(HttpServletRequest request) {
        // Verify the user
        final User user = verifyUser();

        // Parse the ID from the request
        final Long id = Long.parseLong(request.getParameter("id"));

        // Call the service layer to perform the deletion
        transactionBlueprintsService.deleteTransactionBlueprint(id, user);

        // After deletion, you might want to return the updated list of transaction blueprints,
        // or some other relevant data encapsulated in TransactionBlueprintsDTO.
        // This step depends on your application's specific logic and requirements.
        return transactionBlueprintsService.getTransactionBlueprintsDTO(user);
    }


}
