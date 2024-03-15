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

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
        // TODO: Remove this
        userRepository.deleteAll();
        transactionRepository.deleteAll();
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
        }
    }
}
