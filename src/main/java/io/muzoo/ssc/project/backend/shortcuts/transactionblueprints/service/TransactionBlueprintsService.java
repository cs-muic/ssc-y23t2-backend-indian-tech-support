package io.muzoo.ssc.project.backend.shortcuts.transactionblueprints.service;

import java.util.List;
import java.util.stream.Collectors;

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

    public TransactionBlueprintsDTO getRecurringTransactionBlueprintsDTO(User user) {
        final List<TransactionBlueprints> recurringTransactionBlueprintsList = getRecurringTransactionBlueprints(user);
        checkValidTypeAll(recurringTransactionBlueprintsList); // This should still only include valid types, now filtered for RECURRING
        return TransactionBlueprintsDTO.builder()
                .transactionBlueprintsList(recurringTransactionBlueprintsList)
                .build();
    }

    public TransactionBlueprintsDTO getFavoriteTransactionBlueprintsDTO(User user) {
        final List<TransactionBlueprints> favoriteTransactionBlueprintsList = getFavoriteTransactionBlueprints(user);
        checkValidTypeAll(favoriteTransactionBlueprintsList); // This remains unchanged
        return TransactionBlueprintsDTO.builder()
                .transactionBlueprintsList(favoriteTransactionBlueprintsList)
                .build();
    }


    public TransactionBlueprintsDTO postTransactionBlueprintsDTO(HttpServletRequest request, User user) {
        return null;
    }

    public List<TransactionBlueprints> getRecurringTransactionBlueprints(User user) {
        return transactionBlueprintsRepositories.findAllByUserId(user.getId())
                .stream()
                .filter(t -> t.getShortcutType() == Type.RECURRING)
                .collect(Collectors.toList());
    }

    public List<TransactionBlueprints> getFavoriteTransactionBlueprints(User user) {
        // Assuming TransactionBlueprints has a getShortcutType() method and there's an enum or constant for FAVORITES
        return transactionBlueprintsRepositories.findAllByUserId(user.getId())
                .stream()
                .filter(t -> t.getShortcutType() == Type.FAVORITES)
                .collect(Collectors.toList());
    }


    public TransactionBlueprints postTransactionBlueprints(HttpServletRequest request, User user) {
        return null;
    }

}
