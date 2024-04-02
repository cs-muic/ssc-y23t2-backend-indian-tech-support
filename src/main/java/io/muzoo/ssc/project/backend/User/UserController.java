package io.muzoo.ssc.project.backend.User;

import io.muzoo.ssc.project.backend.SimpleResponseDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
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
    public SimpleResponseDTO register(HttpServletRequest request, @RequestParam(value = "avatar", required = false) MultipartFile avatarFile) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String displayName = request.getParameter("display_name");
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
            if (avatarFile != null && !avatarFile.isEmpty()) {
                avatarId = storageService.uploadFile(avatarFile, username);
            } else {
                avatarId = "admin.jpeg";
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
    public SimpleResponseDTO updateUsername(@RequestParam String newUsername) {

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
            if (newUsername.equals(currentUsername)) {
                return SimpleResponseDTO.builder().success(false).message("New username is the same as the current username").build();
            }
            if (newUsername.isEmpty()) {
                return SimpleResponseDTO.builder().success(false).message("Empty Username").build();
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
    public SimpleResponseDTO updateDisplayName(@RequestParam String newDisplayName) {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!(principal instanceof org.springframework.security.core.userdetails.User loggedInUser)) {
                return SimpleResponseDTO.builder().success(false).message("User is not logged in").build();
            }

            User user = userRepository.findByUsername(loggedInUser.getUsername());
            if (user == null) {
                return SimpleResponseDTO.builder().success(false).message("User not found").build();
            }
            if(newDisplayName.isEmpty()) {
                return SimpleResponseDTO.builder().success(false).message("Empty display name").build();
            }
            if(newDisplayName.equals(user.getDisplayName())) {
                return SimpleResponseDTO.builder().success(false).message("New display name is the same as the current display name").build();
            }

            user.setDisplayName(newDisplayName);
            userRepository.save(user);
            return SimpleResponseDTO.builder().success(true).message("Display name updated successfully").build();
        } catch (Exception e) {
            return SimpleResponseDTO.builder().success(false).message("Error updating display name").build();
        }
    }


    @PutMapping("/api/user/update-password")
    public SimpleResponseDTO updatePassword(@RequestParam String newPassword, @RequestParam String confirmPassword) {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!(principal instanceof org.springframework.security.core.userdetails.User loggedInUser)) {
                return SimpleResponseDTO.builder().success(false).message("User is not logged in").build();
            }

            User user = userRepository.findByUsername(loggedInUser.getUsername());
            if (user == null) {
                return SimpleResponseDTO.builder().success(false).message("User not found").build();
            }
            if (!newPassword.equals(confirmPassword)) {
                return SimpleResponseDTO.builder().success(false).message("Passwords do not match").build();
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
                // Capture the old avatar ID
                String oldAvatarId = user.getAvatarId();

                // Proceed with updating the avatar only if it's not the default one
                // Assuming 'admin.jpeg' is stored directly at the root of the bucket
                // and you're storing the complete key in the avatarId field
                if (oldAvatarId != null && !oldAvatarId.equals("admin.jpeg")) {
                    // Upload the new avatar and update the user's avatar ID
                    String newAvatarId = storageService.uploadFile(avatarFile, user.getUsername());
                    user.setAvatarId(newAvatarId);
                    userRepository.save(user);

                    // After successfully uploading the new avatar, delete the old avatar from S3
                    storageService.deleteFile(oldAvatarId);

                    return SimpleResponseDTO.builder().success(true).message("Avatar updated successfully").build();
                } else {
                    // Handle the case where the current avatar is the default one
                    String newAvatarId = storageService.uploadFile(avatarFile, user.getUsername());
                    user.setAvatarId(newAvatarId);
                    userRepository.save(user);

                    return SimpleResponseDTO.builder().success(true).message("Default avatar retained, new avatar uploaded successfully.").build();
                }
            } else {
                return SimpleResponseDTO.builder().success(false).message("Avatar file is empty").build();
            }
        } catch (Exception e) {
            return SimpleResponseDTO.builder().success(false).message("Error updating avatar: " + e.getMessage()).build();
        }
    }

    @GetMapping("api/user/password-check")
    public SimpleResponseDTO checkPassword(@RequestParam String password) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof org.springframework.security.core.userdetails.User loggedInUser)) {
            return SimpleResponseDTO.builder().success(false).message("User is not logged in").build();
        }

        User user = userRepository.findByUsername(loggedInUser.getUsername());
        if (user == null) {
            return SimpleResponseDTO.builder().success(false).message("User not found").build();
        }

        if (passwordEncoder.matches(password, user.getPassword())) {
            return SimpleResponseDTO.builder().success(true).message("You can edit your profile").build();
        } else {
            return SimpleResponseDTO.builder().success(false).message("Password does not match. Cannot edit profile").build();
        }
    }

}
