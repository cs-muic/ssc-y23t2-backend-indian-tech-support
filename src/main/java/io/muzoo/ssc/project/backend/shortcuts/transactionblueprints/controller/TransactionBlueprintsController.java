package io.muzoo.ssc.project.backend.shortcuts.transactionblueprints.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/api/transaction-blueprints/get-transaction-blueprints")
    public TransactionBlueprintsDTO getTransactionBlueprints(){
        final User user = verifyUser();
        return transactionBlueprintsService.getTransactionBlueprints(user);
    }

    @PostMapping("/api/transaction-blueprints/post-transaction-blueprints")
    public TransactionBlueprintsDTO postTransactionBlueprints(HttpServletRequest request){
        final User user = verifyUser();
        return transactionBlueprintsService.postTransactionBlueprints(request,user);
    }

}
