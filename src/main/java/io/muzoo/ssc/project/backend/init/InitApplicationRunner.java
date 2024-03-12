package io.muzoo.ssc.project.backend.init;

import io.muzoo.ssc.project.backend.User.User;
import io.muzoo.ssc.project.backend.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class InitApplicationRunner implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;

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
            user.setDisplay_name("Admin");
            user.setRole("ADMIN");
            userRepository.save(user);
        }

    }
}
