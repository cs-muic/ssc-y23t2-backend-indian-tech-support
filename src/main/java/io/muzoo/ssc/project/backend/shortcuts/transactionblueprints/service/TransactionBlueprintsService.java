package io.muzoo.ssc.project.backend.shortcuts.transactionblueprints.service;

import org.springframework.stereotype.Service;

import io.muzoo.ssc.project.backend.User.User;
import io.muzoo.ssc.project.backend.shortcuts.transactionblueprints.TransactionBlueprints;
import io.muzoo.ssc.project.backend.shortcuts.transactionblueprints.TransactionBlueprintsDTO;
import io.muzoo.ssc.project.backend.shortcuts.transactionblueprints.Type;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class TransactionBlueprintsService {

    public TransactionBlueprintsDTO getTransactionBlueprintsDTO(User user) {
        final TransactionBlueprints transactionBlueprints = getTransactionBlueprints(user);
        if (transactionBlueprints.getShortcutType().equals(Type.NONE)){
            throw new IllegalArgumentException("Invalid Transaction Blueprints Types!");
        }
        return TransactionBlueprintsDTO.builder()
                .id(transactionBlueprints.getId())
                .userId(transactionBlueprints.getUserId())
                .tagId(transactionBlueprints.getTagId2())
                .tagId2(transactionBlueprints.getTagId2())
                .transactionType(transactionBlueprints.getTransactionType().name())
                .shortcutType(transactionBlueprints.getShortcutType().getName())
                .notes(transactionBlueprints.getNotes())
                .value(transactionBlueprints.getValue())
                .build();
    }

    public TransactionBlueprintsDTO postTransactionBlueprintsDTO(HttpServletRequest request, User user) {
        return null;
    }
    
    public TransactionBlueprints getTransactionBlueprints(User user) {
        return null;
    }

    public TransactionBlueprints postTransactionBlueprints(HttpServletRequest request, User user) {
        return null;
    }

}
