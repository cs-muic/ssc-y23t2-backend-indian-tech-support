package io.muzoo.ssc.project.backend.init;

import io.muzoo.ssc.project.backend.Transaction.Transaction;
import io.muzoo.ssc.project.backend.Transaction.TransactionRepository;
import io.muzoo.ssc.project.backend.Transaction.Type;
import io.muzoo.ssc.project.backend.User.User;
import io.muzoo.ssc.project.backend.User.UserRepository;
import io.muzoo.ssc.project.backend.shortcuts.transactionblueprints.TransactionBlueprints;
import io.muzoo.ssc.project.backend.shortcuts.transactionblueprints.repositories.TransactionBlueprintsRepositories;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Component
public class InitApplicationRunner implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager entityManager;


    @Autowired
    private TransactionBlueprintsRepositories transactionBlueprintsRepositories;

    // Creates an admin user in the database when the application starts for the first time.
    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        // TODO: Remove this and @Transactional annotation
        userRepository.deleteAll();
        transactionRepository.deleteAll();
        transactionBlueprintsRepositories.deleteAll();
        resetAutoIncrementValues();
        //TODO: End of Delete

        User admin = userRepository.findByUsername("admin");
        if (admin == null){
            User user = new User();
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("admin"));
            user.setDisplayName("Admin");
            user.setRole("ADMIN");
            userRepository.save(user);
            for (int index = 1; index < 15; index++) {
                Transaction transaction = new Transaction();
                transaction.setUserId(userRepository.findByUsername("admin").getId());
                transaction.setTagId(1);
                transaction.setType(Type.EXPENDITURE);
                transaction.setNotes("Test note");
                transaction.setValue(BigDecimal.valueOf(69.0));
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
                    Timestamp timestamp = Timestamp.valueOf("2018-11-12 01:02:03.123456789");
                    transaction.setTimestamp(timestamp);
                } catch(Exception e) { //this generic but you can control another types of exception
                    // look the origin of excption
                }
                transactionRepository.save(transaction);
            }

            // Create and save 10 shortcut blueprints with varying types
            List<TransactionBlueprints> blueprints = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                TransactionBlueprints blueprint = new TransactionBlueprints();
                blueprint.setUserId(userRepository.findByUsername("admin").getId()); // Assuming a default user ID for simplicity
                blueprint.setTagId(i); // Varied tag IDs for example
                blueprint.setTagId2(i+10); // Additional varied tag ID
                blueprint.setTransactionType(io.muzoo.ssc.project.backend.Transaction.Type.values()[i % io.muzoo.ssc.project.backend.Transaction.Type.values().length]); // Cycling through available transaction types
                blueprint.setShortcutType(io.muzoo.ssc.project.backend.shortcuts.transactionblueprints.Type.values()[i % io.muzoo.ssc.project.backend.shortcuts.transactionblueprints.Type.values().length]); // Cycling through available shortcut types
                blueprint.setNotes("Shortcut blueprint " + i);
                blueprint.setValue(BigDecimal.valueOf(100 + i)); // Example value
                blueprints.add(blueprint);
            }

            transactionBlueprintsRepositories.saveAll(blueprints);
        }
    }

    private void resetAutoIncrementValues() {
        // Adjust the table names as per your actual table names
        entityManager.createNativeQuery("ALTER TABLE user AUTO_INCREMENT = 1;").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE transaction AUTO_INCREMENT = 1;").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE transaction_blueprints AUTO_INCREMENT = 1;").executeUpdate();
        // Add more tables as needed
    }
}
