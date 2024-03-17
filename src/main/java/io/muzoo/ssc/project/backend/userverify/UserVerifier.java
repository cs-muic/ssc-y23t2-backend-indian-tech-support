package io.muzoo.ssc.project.backend.userverify;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;

import io.muzoo.ssc.project.backend.User.User;
import io.muzoo.ssc.project.backend.User.UserRepository;

public class UserVerifier {

    private static UserVerifier instance;

    private UserVerifier (){
        // blank
    }

    public static UserVerifier getInstance(){
        if (instance == null){
            instance = new UserVerifier();
        }
        return instance;
    }

    public User verifyUser(Object principal,UserRepository userRepository){
        if (!(principal instanceof UserDetails)) {
            throw new IllegalStateException("User not authenticated");
        }

        UserDetails userDetails = (UserDetails) principal;
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByUsername(userDetails.getUsername()));
        if (optionalUser.isEmpty()) {
            throw new IllegalStateException("User not found");
        }
        return optionalUser.get();

    }

}
