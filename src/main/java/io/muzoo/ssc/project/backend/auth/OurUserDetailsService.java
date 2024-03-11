package io.muzoo.ssc.project.backend.auth;

import io.muzoo.ssc.project.backend.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class OurUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        io.muzoo.ssc.project.backend.User.User user = userRepository.findByUsername(username);
        if (user != null){
            return User.withUsername(user.getUsername()).password(user.getPassword())
                    .roles(user.getRole()).build();
        } else{
            throw new UsernameNotFoundException("User not found");
        }
    }
}
