package io.muzoo.ssc.project.backend.Tag;

import io.muzoo.ssc.project.backend.User.User;
import io.muzoo.ssc.project.backend.User.UserRepository;
import io.muzoo.ssc.project.backend.history.HistoryDTO;
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
public class TagController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @GetMapping("/api/tag")
    public TagDTO tag() {
        try {
            // The line below has the potential for a NullPointException due to nesting dot notation
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal != null && principal instanceof org.springframework.security.core.userdetails.User) {
                // user is logged in
                org.springframework.security.core.userdetails.User user =
                        (org.springframework.security.core.userdetails.User) principal;
                User u = userRepository.findByUsername(user.getUsername());
                TagDTO output = TagDTO.builder().build();
                output.setLoggedIn(true);
                List<Tag> userTags = tagRepository.findAllByUserId(u.getId());
                userTags.addAll(tagRepository.findAllByUserId(userRepository.findByUsername("admin").getId()));
                output.setTags(userTags);
                return output;
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        // user is not logged in
        return TagDTO.builder()
                .loggedIn(false)
                .build();
    }

    @PostMapping("/api/tag")
    public TagDTO tag(HttpServletRequest request) {
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
            String tagName = request.getParameter("tagName");
            if (tagName.isEmpty()){
                throw new IllegalStateException("Tag name cannot be empty");
            }
            Tag newTag = new Tag();
            newTag.setTagName(tagName);
            newTag.setUserId(user.getId());
            newTag.setDeleted(false);
            tagRepository.save(newTag);
            TagDTO output = TagDTO.builder().build();
            output.setLoggedIn(true);
            List<Tag> userTags = tagRepository.findAllByUserId(user.getId());
            userTags.addAll(tagRepository.findAllByUserId(userRepository.findByUsername("admin").getId()));
            output.setTags(userTags);
            return output;
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        // user is not logged in
            return TagDTO.builder()
                    .loggedIn(false)
                    .build();

    }

}
