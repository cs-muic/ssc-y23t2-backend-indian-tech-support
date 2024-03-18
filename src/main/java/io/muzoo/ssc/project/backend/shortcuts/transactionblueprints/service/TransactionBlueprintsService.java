package io.muzoo.ssc.project.backend.shortcuts.transactionblueprints.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.muzoo.ssc.project.backend.User.User;
import io.muzoo.ssc.project.backend.shortcuts.transactionblueprints.TransactionBlueprints;
import io.muzoo.ssc.project.backend.shortcuts.transactionblueprints.TransactionBlueprintsDTO;
import io.muzoo.ssc.project.backend.shortcuts.transactionblueprints.Type;
import io.muzoo.ssc.project.backend.shortcuts.transactionblueprints.repositories.TransactionBlueprintsRepositories;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class TransactionBlueprintsService {

    @Autowired
    private TransactionBlueprintsRepositories transactionBlueprintsRepositories;

    private void checkValidTypeAll(List<TransactionBlueprints> transactionBlueprintsList){
        transactionBlueprintsList.forEach(
            transactionBlueprints -> {
                if (transactionBlueprints.getShortcutType().equals(Type.NONE)){
                    throw new IllegalArgumentException("Invalid Transaction Blueprints Types!");
                }
            }
        );
    }

    public TransactionBlueprintsDTO getTransactionBlueprintsDTO(User user) {
        final List<TransactionBlueprints> transactionBlueprintsList = getTransactionBlueprints(user);
        checkValidTypeAll(transactionBlueprintsList); // suppose to throw if any one of them have wrong type NONE
        return TransactionBlueprintsDTO.builder()
                   .transactionBlueprintsList(transactionBlueprintsList)
                   .build(); 
    }

    public TransactionBlueprintsDTO postTransactionBlueprintsDTO(HttpServletRequest request, User user) {
        return null;
    }
    
    public List<TransactionBlueprints> getTransactionBlueprints(User user) {
        return transactionBlueprintsRepositories.findAllByUserId(user.getId());
    }

    public TransactionBlueprints postTransactionBlueprints(HttpServletRequest request, User user) {
        return null;
    }

}
