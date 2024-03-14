package io.muzoo.ssc.project.backend.init;

import io.muzoo.ssc.project.backend.Transaction.Transaction;
import io.muzoo.ssc.project.backend.Transaction.TransactionRepository;
import io.muzoo.ssc.project.backend.Transaction.Type;
import io.muzoo.ssc.project.backend.User.User;
import io.muzoo.ssc.project.backend.User.UserRepository;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Component
public class InitApplicationRunner implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Creates an admin user in the database when the application starts for the first time.
    @Override
    public void run(ApplicationArguments args) throws Exception {
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
                transaction.setTimestamp(new java.util.Date(2024, Calendar.FEBRUARY, 1, 1, 1, 1));
                transactionRepository.save(transaction);
            }
        }
    }
}
