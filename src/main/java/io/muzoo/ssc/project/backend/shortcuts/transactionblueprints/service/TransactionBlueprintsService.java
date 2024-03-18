package io.muzoo.ssc.project.backend.shortcuts.transactionblueprints.service;

import java.math.BigDecimal;
import java.util.ArrayList;
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
        final List<TransactionBlueprints> transactionBlueprintsList = postTransactionBlueprints(request, user);
        checkValidTypeAll(transactionBlueprintsList);
        return TransactionBlueprintsDTO.builder()
                .transactionBlueprintsList(transactionBlueprintsList)
                .build();
    }
    
    public List<TransactionBlueprints> getTransactionBlueprints(User user) {
        return transactionBlueprintsRepositories.findAllByUserId(user.getId());
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


    public List<TransactionBlueprints> postTransactionBlueprints(HttpServletRequest request, User user) {
        // this list contain only one item, is there just to conform to the dto definition
        final List<TransactionBlueprints> transactionBlueprintsList = new ArrayList<>();
        final TransactionBlueprints transactionBlueprints = new TransactionBlueprints();
        transactionBlueprintsList.add(transactionBlueprints);

        try {
            final Long userId = Long.parseLong(request.getParameter("userId"));
            transactionBlueprints.setUserId(userId);
        }
        catch (NumberFormatException e){
            e.printStackTrace();
            throw new IllegalArgumentException("Illegal UserId Format");
        }
        try {
            final Long tagId = Long.parseLong(request.getParameter("tagId"));
            transactionBlueprints.setTagId(tagId);
        }
        catch (NumberFormatException e){
            e.printStackTrace();
            throw new IllegalArgumentException("Illegal TagId Format");
        }
        try {
            final Long tagId2 = Long.parseLong(request.getParameter("tagId2"));
            transactionBlueprints.setTagId2(tagId2);
        }
        catch (NumberFormatException e){
            e.printStackTrace();
            throw new IllegalArgumentException("Illegal TagId2 Format");
        }
        {
            final io.muzoo.ssc.project.backend.Transaction.Type transactionType = io.muzoo.ssc.project.backend.Transaction.Type.parseType(request.getParameter("transactionType"));
            if (transactionType.equals(io.muzoo.ssc.project.backend.Transaction.Type.NONE)){
                throw new IllegalArgumentException("Illegal Trasaction Type Format");
            }
            transactionBlueprints.setTransactionType(transactionType);
        }
        {
            final Type transactionType = Type.getType(request.getParameter("shortcutType"));
            if (transactionType.equals(Type.NONE)){
                throw new IllegalArgumentException("Illegal Trasaction Type Format");
            }
            transactionBlueprints.setShortcutType(transactionType);
        }
        {
            final String notes = request.getParameter("notes");
            if (notes == null){
                throw new IllegalArgumentException("Illegal Notes Format, Can be empty but not null");
            }
            transactionBlueprints.setNotes(notes);
        }
        try {
            final BigDecimal value = new BigDecimal(request.getParameter("value"));
            transactionBlueprints.setValue(value);
        }
        catch (NumberFormatException e){
            e.printStackTrace();
            throw new IllegalArgumentException("Illegal Values Formats");
        }
        try {
            final Integer datedateofMonthRecurring = Integer.parseInt(request.getParameter("dateofMonthRecurring"));
            transactionBlueprints.setDateofMonthRecurring(datedateofMonthRecurring);
        }
        catch (NumberFormatException e){
            e.printStackTrace();
            throw new IllegalArgumentException("Illegal Dates of Month Formats");
        }
        {
            final String resourceURI = request.getParameter("resourceURI");
            if (resourceURI == null){
                throw new IllegalArgumentException("Illegal resourceURI Format, Can be empty but not NULL!");
            }
            transactionBlueprints.setResourceURI(resourceURI);
        }

        // save to database
        transactionBlueprintsRepositories.saveAll(transactionBlueprintsList);

        return transactionBlueprintsList;
    }

}
