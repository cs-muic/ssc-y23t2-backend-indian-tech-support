package io.muzoo.ssc.project.backend.shortcuts.recurring;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.muzoo.ssc.project.backend.Transaction.Transaction;
import io.muzoo.ssc.project.backend.Transaction.TransactionRepository;
import io.muzoo.ssc.project.backend.shortcuts.transactionblueprints.TransactionBlueprints;
import io.muzoo.ssc.project.backend.shortcuts.transactionblueprints.Type;
import io.muzoo.ssc.project.backend.shortcuts.transactionblueprints.repositories.TransactionBlueprintsRepositories;
import io.muzoo.ssc.project.backend.timestamp.Timestamper;

@Service
public class RecurringsPublisher {
    
    @Autowired
    private TransactionBlueprintsRepositories transactionBlueprintsRepositories;

    @Autowired
    private TransactionRepository transactionRepository;
    
    public Stream<TransactionBlueprints> getAllTodaysScheuduled(){

        return transactionBlueprintsRepositories.findAllByShortcutType(Type.RECURRING)
                .stream()
                .filter(transactionBlueprints -> {
                    return RecurringTimestamp.dateOrMaxDateOfMonth(transactionBlueprints.getDateofMonthRecurring())
                    == java.time.LocalDate.now().getDayOfMonth();
                    
                }
        );

    }

    public Timestamp getTimeStamp(){
        return Timestamper.getTimestamp();
    }

    public Transaction createTransactions(TransactionBlueprints transactionBlueprints){

        final Transaction transaction = new Transaction();
        transaction.setUserId(transactionBlueprints.getUserId());
        transaction.setTagId(transactionBlueprints.getTagId());
        transaction.setTagId2(transactionBlueprints.getTagId2());
        transaction.setType(transactionBlueprints.getTransactionType());
        transaction.setNotes(transactionBlueprints.getNotes());
        transaction.setValue(transactionBlueprints.getValue());
        transaction.setTimestamp(getTimeStamp());
        return transaction;

    }

    public void publishRecurrings(){

        getAllTodaysScheuduled().map(transactionBlueprints -> {
            return createTransactions(transactionBlueprints);
        }).forEach(transactions -> {
            transactionRepository.save(transactions);
        });

    }

}
