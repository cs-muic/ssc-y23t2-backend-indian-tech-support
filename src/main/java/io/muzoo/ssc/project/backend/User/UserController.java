package io.muzoo.ssc.project.backend.User;

import io.muzoo.ssc.project.backend.SimpleResponseDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StorageService storageService;

    @PostMapping("/api/signup")
    public SimpleResponseDTO register(HttpServletRequest request, @RequestParam("avatar") MultipartFile avatarFile) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String displayName = request.getParameter("display_name");
        System.out.println(username + " " + password + " " + displayName);

        try {
            // Check if the username already exists
            User existingUser = userRepository.findByUsername(username);
            if (existingUser != null) {
                return SimpleResponseDTO.builder().success(false).message("Username already taken").build();
            }

            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof org.springframework.security.core.userdetails.User) {
                request.logout();
            }

            String avatarId;
            if (!avatarFile.isEmpty()) {
                avatarId = storageService.uploadFile(avatarFile, username);
            } else {
                avatarId = "default_avatar.jpeg";
            }

            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setDisplayName(displayName);
            user.setRole("USER");
            user.setAvatarId(avatarId); // Set avatar ID after successful upload

            userRepository.save(user);

            request.login(username, password);

            return SimpleResponseDTO.builder().success(true).message("Register Successful").build();
        } catch (ServletException e) {
            return SimpleResponseDTO.builder().success(false).message(e.getMessage()).build();
        }
    }

    @PutMapping("/api/user/update-username")
    public SimpleResponseDTO updateUsername(HttpServletRequest request, @RequestParam("newUsername") String newUsername) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal != null && principal instanceof UserDetails userDetails) {
            String currentUsername = userDetails.getUsername();

            // Fetch the current user by username
            User currentUser = userRepository.findByUsername(currentUsername);
            if (currentUser == null) {
                return SimpleResponseDTO.builder().success(false).message("User not found").build();
            }

            // Check if the new username is already taken
            User existingUser = userRepository.findByUsername(newUsername);
            if (existingUser != null) {
                return SimpleResponseDTO.builder().success(false).message("Username already taken").build();
            }

            // Update username
            currentUser.setUsername(newUsername);
            userRepository.save(currentUser);

            return SimpleResponseDTO.builder().success(true).message("Username updated successfully").build();
        } else {
            return SimpleResponseDTO.builder().success(false).message("User is not logged in").build();
        }
    }

    @PutMapping("/api/user/update-display-name")
    public SimpleResponseDTO updateDisplayName(HttpServletRequest request, @RequestParam("newDisplayName") String newDisplayName) {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!(principal instanceof org.springframework.security.core.userdetails.User loggedInUser)) {
                return SimpleResponseDTO.builder().success(false).message("User is not logged in").build();
            }

            User user = userRepository.findByUsername(loggedInUser.getUsername());
            if (user == null) {
                return SimpleResponseDTO.builder().success(false).message("User not found").build();
            }

            user.setDisplayName(newDisplayName);
            userRepository.save(user);
            return SimpleResponseDTO.builder().success(true).message("Display name updated successfully").build();
        } catch (Exception e) {
            return SimpleResponseDTO.builder().success(false).message("Error updating display name").build();
        }
    }


    @PutMapping("/api/user/update-password")
    public SimpleResponseDTO updatePassword(HttpServletRequest request, @RequestParam("newPassword") String newPassword) {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!(principal instanceof org.springframework.security.core.userdetails.User loggedInUser)) {
                return SimpleResponseDTO.builder().success(false).message("User is not logged in").build();
            }

            User user = userRepository.findByUsername(loggedInUser.getUsername());
            if (user == null) {
                return SimpleResponseDTO.builder().success(false).message("User not found").build();
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return SimpleResponseDTO.builder().success(true).message("Password updated successfully").build();
        } catch (Exception e) {
            return SimpleResponseDTO.builder().success(false).message("Error updating password").build();
        }
    }

    @PostMapping("/api/user/update-avatar")
    public SimpleResponseDTO updateAvatar(HttpServletRequest request, @RequestParam("avatar") MultipartFile avatarFile) {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!(principal instanceof org.springframework.security.core.userdetails.User loggedInUser)) {
                return SimpleResponseDTO.builder().success(false).message("User is not logged in").build();
            }

            User user = userRepository.findByUsername(loggedInUser.getUsername());
            if (user == null) {
                return SimpleResponseDTO.builder().success(false).message("User not found").build();
            }

            if (!avatarFile.isEmpty()) {
                String avatarId = storageService.uploadFile(avatarFile, user.getUsername());
                user.setAvatarId(avatarId);
                userRepository.save(user);
                return SimpleResponseDTO.builder().success(true).message("Avatar updated successfully").build();
            } else {
                return SimpleResponseDTO.builder().success(false).message("Avatar file is empty").build();
            }
        } catch (Exception e) {
            return SimpleResponseDTO.builder().success(false).message("Error updating avatar").build();
        }
    }

}
