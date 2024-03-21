package io.muzoo.ssc.project.backend.shortcuts.transactionblueprints.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties.Consumer.DeadLetterPolicy;
import org.springframework.stereotype.Service;

import io.micrometer.common.util.StringUtils;
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

    private TransactionBlueprintsDTO listsToDTO(List<TransactionBlueprints> transactionBlueprintsList){
        checkValidTypeAll(transactionBlueprintsList);
        return TransactionBlueprintsDTO.builder()
                .transactionBlueprintsList(transactionBlueprintsList)
                .build();
    }

    private String getNewResourceURI(HttpServletRequest request,TransactionBlueprints transactionBlueprints, User user){
        // TODO: wire this up poroperly
        return "";

    }

    private TransactionBlueprints getDefaultTransactionBlueprints(HttpServletRequest request, User user){
        final String transactionBlueprintsIDString = request.getParameter("transactionBlueprintsID");
        if (transactionBlueprintsIDString == null){
            return null;
        }
        else {
            final Long transactionBlueprintsID = Long.parseLong(transactionBlueprintsIDString);
            return transactionBlueprintsRepositories.getReferenceById(transactionBlueprintsID);
        }
    }

    private TransactionBlueprints createSafeTransactionBlueprints(HttpServletRequest request, User user){

        final TransactionBlueprints transactionBlueprints = new TransactionBlueprints();

        try {
            final Long userId = user.getId();
            transactionBlueprints.setUserId(userId);
        } catch (NumberFormatException | NullPointerException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Illegal UserId Format");
        }
        try {
            final Long tagId = Long.parseLong(request.getParameter("tagId"));
            transactionBlueprints.setTagId(tagId);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Illegal TagId Format");
        }
        try {
            final Long tagId2 = Long.parseLong(request.getParameter("tagId2"));
            transactionBlueprints.setTagId2(tagId2);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Illegal TagId2 Format");
        }
        {
            final io.muzoo.ssc.project.backend.Transaction.Type transactionType = io.muzoo.ssc.project.backend.Transaction.Type.parseType(request.getParameter("transactionType"));
            if (transactionType.equals(io.muzoo.ssc.project.backend.Transaction.Type.NONE)) {
                throw new IllegalArgumentException("Illegal Trasaction Type Format");
            }
            transactionBlueprints.setTransactionType(transactionType);
        }
        {
            final Type transactionType = Type.getType(request.getParameter("shortcutType"));
            if (transactionType.equals(Type.NONE)) {
                throw new IllegalArgumentException("Illegal Trasaction Type Format");
            }
            transactionBlueprints.setShortcutType(transactionType);
        }
        {
            final String notes = request.getParameter("notes");
            if (notes == null) {
                throw new IllegalArgumentException("Illegal Notes Format, Can be empty but not null");
            }
            transactionBlueprints.setNotes(notes);
        }
        try {
            final BigDecimal value = new BigDecimal(request.getParameter("value"));
            transactionBlueprints.setValue(value);
        } catch (NumberFormatException | NullPointerException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Illegal Values Formats");
        }
        try {
            final String dateOfMonthString  = request.getParameter("dateofMonthRecurring");
            if (StringUtils.isBlank(dateOfMonthString)){
                throw new IllegalArgumentException("Illegal Dates of Month Formats, Cannot Be Null");    
            }
            final Integer datedateofMonthRecurring = Integer.parseInt(dateOfMonthString);
            transactionBlueprints.setDateofMonthRecurring(datedateofMonthRecurring);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Illegal Dates of Month Formats");
        }
        {
            /* does not suppose to allow users to send resourceURI, is managed in the backside */
            // final String resourceURI = request.getParameter("resourceURI");
            // if (StringUtils.isBlank(resourceURI)) {
            //     throw new IllegalArgumentException("Illegal resourceURI Format, Can Not Be NULL!");
            // }
            // transactionBlueprints.setResourceURI(resourceURI);
            transactionBlueprints.setResourceURI(getNewResourceURI(request,transactionBlueprints,user));
        }

        return transactionBlueprints;

    }

    private TransactionBlueprints createPartialTransactionBlueprintsFromDefaultsBlueprints(HttpServletRequest request, User user, TransactionBlueprints defaultBlueprints){

        final TransactionBlueprints transactionBlueprints = new TransactionBlueprints();

        try {
            final Long userId = user.getId();
            transactionBlueprints.setUserId(userId);
        } catch (NumberFormatException | NullPointerException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Illegal UserId Format");
        }
        try {
            final String tagIdString = request.getParameter("tagId");
            if (tagIdString == null){
                transactionBlueprints.setTagId(defaultBlueprints.getTagId());
            }
            else {
                final Long tagId = Long.parseLong(tagIdString);
                transactionBlueprints.setTagId(tagId);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Illegal TagId Format");
        }
        try {
            final String tagId2String = request.getParameter("tagId2");
            if (tagId2String == null){
                transactionBlueprints.setTagId2(defaultBlueprints.getTagId2());
            }
            else {
                final Long tagId2 = Long.parseLong(tagId2String);
                transactionBlueprints.setTagId2(tagId2);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Illegal TagId2 Format");
        }
        {
            final String transactionTypeString = request.getParameter("transactionType");
            if (transactionTypeString == null){
                transactionBlueprints.setTransactionType(defaultBlueprints.getTransactionType());
            }
            else {
                final io.muzoo.ssc.project.backend.Transaction.Type transactionType = io.muzoo.ssc.project.backend.Transaction.Type.parseType(transactionTypeString);
                if (transactionType.equals(io.muzoo.ssc.project.backend.Transaction.Type.NONE)) {
                    throw new IllegalArgumentException("Illegal Trasaction Type Format");
                }
                transactionBlueprints.setTransactionType(transactionType);
            }
        }
        {
            final String shortcutTypeString = request.getParameter("shortcutType");
            if (shortcutTypeString == null){
                transactionBlueprints.setShortcutType(defaultBlueprints.getShortcutType());
            }
            else {
                final Type shortcutType = Type.getType(shortcutTypeString);
                if (shortcutType.equals(Type.NONE)) {
                    throw new IllegalArgumentException("Illegal Shortcut Type Format");
                }
                transactionBlueprints.setShortcutType(shortcutType);
            }
        }
        {
            final String notesString = request.getParameter("notes");
            if (notesString == null){
                transactionBlueprints.setNotes(defaultBlueprints.getNotes());
            }
            else {
                final String notes = notesString;
                // if (notes == null) {
                //     throw new IllegalArgumentException("Illegal Notes Format, Can be empty but not null");
                // }
                transactionBlueprints.setNotes(notes);
            }
        }
        try {
            final String valueString = request.getParameter("value");
            if (valueString == null){
                transactionBlueprints.setValue(defaultBlueprints.getValue());
            }
            else {
                final BigDecimal value = new BigDecimal(valueString);
                transactionBlueprints.setValue(value);
            }
        } catch (NumberFormatException | NullPointerException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Illegal Values Formats");
        }
        try {
            final String dateOfMonthStringString = request.getParameter("dateofMonthRecurring");
            if (dateOfMonthStringString == null){
                transactionBlueprints.setDateofMonthRecurring(defaultBlueprints.getDateofMonthRecurring());
            }
            else {
                final String dateOfMonthString  = dateOfMonthStringString;
                if (StringUtils.isBlank(dateOfMonthString)){
                    throw new IllegalArgumentException("Illegal Dates of Month Formats, Cannot Be Null");    
                }
                final Integer datedateofMonthRecurring = Integer.parseInt(dateOfMonthString);
                transactionBlueprints.setDateofMonthRecurring(datedateofMonthRecurring);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Illegal Dates of Month Formats");
        }
        {
            /*
             * currently use same resourceURI as default transactions blueprints
            */
            // final String resourceURIString = request.getParameter("resourceURI");
            // if (resourceURIString == null){
                transactionBlueprints.setResourceURI(defaultBlueprints.getResourceURI());
            // }
            // else {
            //     final String resourceURI = resourceURIString;
            //     if (StringUtils.isBlank(resourceURI)) {
            //         throw new IllegalArgumentException("Illegal resourceURI Format, Can Not Be NULL!");
            //     }
            //     transactionBlueprints.setResourceURI(resourceURI);
            // }
        }

        return transactionBlueprints;

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
        // final TransactionBlueprints transactionBlueprints = new TransactionBlueprints();
        final TransactionBlueprints transactionBlueprints = createSafeTransactionBlueprints(request, user);
        transactionBlueprintsList.add(transactionBlueprints);

        /* assign new resourceURI */
        final String resourceURI = getNewResourceURI(request, transactionBlueprints, user);
        transactionBlueprints.setResourceURI(resourceURI);

        // save to database
        transactionBlueprintsRepositories.saveAll(transactionBlueprintsList);

        return transactionBlueprintsList;
    }

    public TransactionBlueprintsDTO getTransactionBlueprintsDTO(User user) {
        // Assuming there's a method in your repository to find all transaction blueprints by the user
        List<TransactionBlueprints> transactionBlueprintsList = transactionBlueprintsRepositories.findByUserId(user.getId());

        // Build and return the DTO
        return TransactionBlueprintsDTO.builder()
                .transactionBlueprintsList(transactionBlueprintsList)
                .build();
    }

    public boolean deleteTransactionBlueprint(Long id, User user) {
        // Optional check if the transaction blueprint exists and belongs to the user
        return transactionBlueprintsRepositories.findById(id)
                .filter(blueprint -> blueprint.getUserId() == (user.getId()))
                .map(blueprint -> {
                    transactionBlueprintsRepositories.delete(blueprint);
                    return true; // Successfully deleted
                })
                .orElse(false); // Not found or not owned by the user, not deleted
    }

    public List<TransactionBlueprints> createTransactionBlueprintRecurrings(HttpServletRequest request, User user) {
        /* implementations is the same one for this */
        return postTransactionBlueprints(request, user);
    }

    public List<TransactionBlueprints> editTransactionBlueprintRecurrings(HttpServletRequest request, User user) {
        final TransactionBlueprints defaultsTransactionBlueprints = getDefaultTransactionBlueprints(request, user);
        if (defaultsTransactionBlueprints == null){
            throw new IllegalArgumentException("No Such Transaction Blueprints (transaction blueprints id not found)");
        }
        final TransactionBlueprints editedTransactionBlueprints = createPartialTransactionBlueprintsFromDefaultsBlueprints(request,user,defaultsTransactionBlueprints);
        final List<TransactionBlueprints> transactionBlueprintsList = new ArrayList<>();
        transactionBlueprintsList.add(editedTransactionBlueprints);
        return transactionBlueprintsList;
    }

    public List<TransactionBlueprints> deleteTransactionBlueprintRecurrings(HttpServletRequest request, User user) {
        final TransactionBlueprints transactionBlueprintsToDelete = getDefaultTransactionBlueprints(request, user);
        if (transactionBlueprintsToDelete == null){
            throw new IllegalArgumentException("No Such Transaction Blueprints (transaction blueprints id not found)");
        }
        /* delete from database */
        final List<Long> transactionBlueprintsIDList = new ArrayList<>();
        transactionBlueprintsIDList.add(transactionBlueprintsToDelete.getId());
        transactionBlueprintsRepositories.deleteAllById(transactionBlueprintsIDList);
        final List<TransactionBlueprints> transactionBlueprintsList = new ArrayList<>();
        transactionBlueprintsList.add(transactionBlueprintsToDelete);
        return transactionBlueprintsList;
    }

    public List<TransactionBlueprints> createTransactionBlueprintFavorites(HttpServletRequest request, User user) {
        /* implementations is the same one for this */
        return postTransactionBlueprints(request, user);
    }

    public TransactionBlueprintsDTO editTransactionBlueprintFavorites(HttpServletRequest request, User user) {
        return null;
    }

    public TransactionBlueprintsDTO deleteTransactionBlueprintFavorites(HttpServletRequest request, User user) {
        return null;
    }

    public TransactionBlueprintsDTO createTransactionBlueprintDTORecurrings(HttpServletRequest request, User user) {
        final List<TransactionBlueprints> transactionBlueprintsList = createTransactionBlueprintRecurrings(request, user);
        final TransactionBlueprintsDTO transactionBlueprintsDTO = listsToDTO(transactionBlueprintsList);
        return transactionBlueprintsDTO;
    }

    public TransactionBlueprintsDTO editTransactionBlueprintDTORecurrings(HttpServletRequest request, User user) {
        final List<TransactionBlueprints> transactionBlueprintsList = editTransactionBlueprintRecurrings(request, user);
        final TransactionBlueprintsDTO transactionBlueprintsDTO = listsToDTO(transactionBlueprintsList);
        return transactionBlueprintsDTO;
    }

    public TransactionBlueprintsDTO deleteTransactionBlueprintDTORecurrings(HttpServletRequest request, User user) {
        final List<TransactionBlueprints> transactionBlueprintsList = deleteTransactionBlueprintRecurrings(request, user);
        final TransactionBlueprintsDTO transactionBlueprintsDTO = listsToDTO(transactionBlueprintsList);
        return transactionBlueprintsDTO;
    }

    public TransactionBlueprintsDTO createTransactionBlueprintDTOFavorites(HttpServletRequest request, User user) {
        return null;
    }

    public TransactionBlueprintsDTO editTransactionBlueprintDTOFavorites(HttpServletRequest request, User user) {
        return null;
    }

    public TransactionBlueprintsDTO deleteTransactionBlueprintDTOFavorites(HttpServletRequest request, User user) {
        return null;
    }

}
