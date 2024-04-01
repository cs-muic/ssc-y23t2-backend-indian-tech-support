package io.muzoo.ssc.project.backend.Tag;

import io.muzoo.ssc.project.backend.User.User;
import io.muzoo.ssc.project.backend.User.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class SecondaryTagController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecondaryTagRepository secondaryTagRepository;
    @GetMapping("/api/secondary_tag")
    public SecondaryTagDTO getAll(HttpServletRequest request) {
        try {
            // The line below has the potential for a NullPointException due to nesting dot notation
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal != null && principal instanceof org.springframework.security.core.userdetails.User) {
                // user is logged in
                org.springframework.security.core.userdetails.User user =
                        (org.springframework.security.core.userdetails.User) principal;
                User u = userRepository.findByUsername(user.getUsername());

                // Extracting and parsing request parameters
                String tagIdParam = request.getParameter("tagId");
                long tagId = 0; // Default value

                try {
                    // Only parse if the parameters are not null and not empty
                    if (tagIdParam != null && !tagIdParam.isEmpty()) {
                        tagId = Long.parseLong(tagIdParam);
                    }
                } catch (NumberFormatException e) {
                    // Log error or handle the case where parameters are invalid
                    System.err.println("Error parsing tagId or tagId2 from request parameters");
                }

                SecondaryTagDTO output = SecondaryTagDTO.builder().build();
                output.setLoggedIn(true);
                output.setSecondaryTags(secondaryTagRepository.findAllByTagId(tagId));
                return output;
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        // user is not logged in
        return SecondaryTagDTO.builder()
                .loggedIn(false)
                .build();
    }

    @PostMapping("/api/secondary_tag")
    public SecondaryTagDTO addTag(HttpServletRequest request) {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!(principal instanceof UserDetails)) {
                throw new IllegalStateException("User not authenticated");
            }

            UserDetails userDetails = (UserDetails) principal;
            Optional<User> optionalUser = Optional.ofNullable(userRepository.findByUsername(userDetails.getUsername()));
            if (!optionalUser.isPresent()) {
                throw new IllegalStateException("User not found");
            }
            User user = optionalUser.get();
            // Extracting and parsing request parameters
            String tagIdParam = request.getParameter("tagId");
            String secondaryTagName = request.getParameter("secondaryTagName");

            long tagId = 0;
            try {
                // Only parse if the parameters are not null and not empty
                if (tagIdParam != null && !tagIdParam.isEmpty()) {
                    tagId = Long.parseLong(tagIdParam);
                }
            } catch (NumberFormatException e) {
                // Log error or handle the case where parameters are invalid
                System.err.println("Error parsing tagId or tagId2 from request parameters");
            }
            if (secondaryTagName.isEmpty()){
                throw new IllegalStateException("Secondary tag name cannot be empty");
            }
            SecondaryTag newSecondaryTag = new SecondaryTag();
            newSecondaryTag.setSecondaryTagName(secondaryTagName);
            newSecondaryTag.setTagId(tagId);
            newSecondaryTag.setDeleted(false);
            secondaryTagRepository.save(newSecondaryTag);
            SecondaryTagDTO output = SecondaryTagDTO.builder().build();
            output.setLoggedIn(true);
            output.setSecondaryTags(secondaryTagRepository.findAllByTagId(tagId));
            return output;
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        // user is not logged in
        return SecondaryTagDTO.builder()
                .loggedIn(false)
                .build();

    }

    @GetMapping("/api/user-all-secondary-tags")
    public SecondaryTagDTO getAllUserSecondaryTags() {
        SecondaryTagDTO output = SecondaryTagDTO.builder().build();
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                User user = userRepository.findByUsername(userDetails.getUsername());

                if (user != null) {
                    List<SecondaryTag> userSecondaryTags = secondaryTagRepository.findAllByUserId(user.getId());
                    output.setSecondaryTags(userSecondaryTags);
                    output.setLoggedIn(true);
                    return output;
                } else {
                    throw new IllegalStateException("User not found");
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching user's secondary tags: " + e.toString());
        }

        output.setLoggedIn(false);
        return output;
    }


}
