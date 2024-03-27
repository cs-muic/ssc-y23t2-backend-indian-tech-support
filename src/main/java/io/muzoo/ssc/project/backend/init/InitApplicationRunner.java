package io.muzoo.ssc.project.backend.init;

import io.muzoo.ssc.project.backend.Tag.SecondaryTagRepository;
import io.muzoo.ssc.project.backend.Tag.TagRepository;
import io.muzoo.ssc.project.backend.TargetBudget.TargetBudgetRepository;
import io.muzoo.ssc.project.backend.Transaction.Transaction;
import io.muzoo.ssc.project.backend.Transaction.TransactionRepository;
import io.muzoo.ssc.project.backend.Transaction.Type;
import io.muzoo.ssc.project.backend.User.User;
import io.muzoo.ssc.project.backend.User.UserRepository;
import io.muzoo.ssc.project.backend.shortcuts.transactionblueprints.TransactionBlueprints;
import io.muzoo.ssc.project.backend.shortcuts.transactionblueprints.repositories.TransactionBlueprintsRepositories;
import io.muzoo.ssc.project.backend.Tag.Tag;
import io.muzoo.ssc.project.backend.Tag.SecondaryTag;
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
import java.util.*;

@Component
public class InitApplicationRunner implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TargetBudgetRepository targetBudgetRepository;

    @Autowired
    private SecondaryTagRepository secondaryTagRepository;

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
        tagRepository.deleteAll();
        secondaryTagRepository.deleteAll();
        targetBudgetRepository.deleteAll();
        resetAutoIncrementValues();
        //TODO: End of Delete

        User admin = userRepository.findByUsername("admin"); // DO NOT DELETE THIS! TAG RELIES ON IT
        if (admin == null){
            Random random = new Random();
            User user = new User();
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("admin"));
            user.setDisplayName("Admin");
            user.setRole("ADMIN");
            user.setAvatarId("admin.jpeg");
            userRepository.save(user);
            List<String> mainTags = Arrays.asList("Income", "Housing", "Utilities", "Food" ,"Transportation",
                    "Healthcare", "Insurance", "Personal Spending", "Recreation & Entertainment",
                    "Savings & Investments");
            List<String> secondaryTags = Arrays.asList("Salary", "Investments", "Other Income", "Rent/Mortgage",
                    "Property Taxes", "Maintenance/Repairs", "Electricity", "Water", "Internet", "Groceries",
                    "Dining Out", "Coffee Shops", "Fuel", "Public Transport", "Vehicle Maintenance", "Doctor Visits",
                    "Medications", "Health Insurance", "Life Insurance", "Property Insurance", "Car Insurance",
                    "Clothing", "Gadgets", "Hobbies", "Movies", "Concerts", "Sporting Events", "Savings Account",
                    "Stock Market Investments", "Retirement Savings");
            // Map main tags to their corresponding secondary tags using indices
            Map<String, List<Integer>> tagMappings = new HashMap<>();
            tagMappings.put("Income", Arrays.asList(0, 1, 2));
            tagMappings.put("Housing", Arrays.asList(3, 4, 5));
            tagMappings.put("Utilities", Arrays.asList(6, 7, 8));
            tagMappings.put("Food", Arrays.asList(9, 10, 11));
            tagMappings.put("Transportation", Arrays.asList(12, 13, 14));
            tagMappings.put("Healthcare", Arrays.asList(15, 16));
            tagMappings.put("Insurance", Arrays.asList(17, 18, 19, 20));
            tagMappings.put("Personal Spending", Arrays.asList(21, 22, 23));
            tagMappings.put("Recreation & Entertainment", Arrays.asList(24, 25, 26));
            tagMappings.put("Savings & Investments", Arrays.asList(27, 28, 29));

            for (String mainTag : mainTags) {
                Tag tag = new Tag();
                tag.setTagName(mainTag);
                tag.setDeleted(false);
                tag.setUserId(userRepository.findByUsername("admin").getId()); // Assuming this returns a valid user
                Tag savedTag = tagRepository.save(tag);

                List<Integer> secondaryIndices = tagMappings.get(mainTag);
                for (Integer index : secondaryIndices) {
                    SecondaryTag secondaryTag = new SecondaryTag();
                    secondaryTag.setSecondaryTagName(secondaryTags.get(index));
                    secondaryTag.setDeleted(false);
                    secondaryTag.setTagId(savedTag.getId()); // Assuming this sets the association correctly
                    secondaryTagRepository.save(secondaryTag);
                }
            }
            // TODO: Remove this for loop for final product
            for (int index = 1; index < 15; index++) {
                Transaction transaction = new Transaction();
                transaction.setUserId(userRepository.findByUsername("admin").getId());
                transaction.setTagId(random.nextInt(1, 11));
                transaction.setTagId2((random.nextInt(1, 11)));
                transaction.setType(Type.EXPENDITURE);
                transaction.setNotes("Test note");
                transaction.setValue(BigDecimal.valueOf(69.0));
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
                    Timestamp timestamp = Timestamp.valueOf("2024-03-02 01:02:03.123456789");
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
        entityManager.createNativeQuery("ALTER TABLE tag AUTO_INCREMENT = 1;").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE secondary_tag AUTO_INCREMENT = 1;").executeUpdate();
        // Add more tables as needed
    }
}
