package io.muzoo.ssc.project.backend.TargetBudget;

import io.muzoo.ssc.project.backend.User.User;
import io.muzoo.ssc.project.backend.User.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
public class TargetBudgetController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TargetBudgetRepository targetBudgetRepository;

    public User verifyUser(Object principal) {
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

    @GetMapping("/api/targetBudget")
    public TargetBudgetDTO getTransaction() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = verifyUser(principal);
        TargetBudget tb = targetBudgetRepository.findByUserId(user.getId());
        if (tb == null) {
            return TargetBudgetDTO.builder().found(false).build();
        }
        return TargetBudgetDTO.builder()
                .budget(tb.getBudget())
                .target(tb.getTarget())
                .emptyTarget(BigDecimal.ZERO.compareTo(tb.getTarget()) > 0)
                .emptyBudget(BigDecimal.ZERO.compareTo(tb.getBudget()) > 0)
                .found(true)
                .build();
    }

    @PostMapping("/api/createTargetBudget")
    public TargetBudgetDTO createTransaction(@RequestParam float userBudget, @RequestParam float userTarget) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = verifyUser(principal);
        BigDecimal target = new BigDecimal(userTarget);
        BigDecimal budget = new BigDecimal(userBudget);

        // Check if target or budget is null or less than or equal to 0
        if (target.compareTo(BigDecimal.ZERO) <= 0) {
            target = BigDecimal.ZERO;
        }
        if (budget.compareTo(BigDecimal.ZERO) <= 0) {
            budget = BigDecimal.ZERO;
        }

        // Check if a TargetBudget object already exists for the user
        TargetBudget tb = targetBudgetRepository.findByUserId(user.getId());
        if (tb == null) {
            // If not, create a new TargetBudget object and set its fields
            tb = new TargetBudget();
            tb.setUserId(user.getId());
        }

        // Update the target and budget fields
        tb.setTarget(target);
        tb.setBudget(budget);

        // Save the TargetBudget object in the database
        tb = targetBudgetRepository.save(tb);

        // Create a TargetBudgetDTO object and set its fields
        TargetBudgetDTO dto = TargetBudgetDTO.builder()
                .budget(tb.getBudget())
                .target(tb.getTarget())
                .emptyTarget(tb.getTarget().compareTo(BigDecimal.ZERO) <= 0)
                .emptyBudget(tb.getBudget().compareTo(BigDecimal.ZERO) <= 0)
                .found(true)
                .build();

        // Return the TargetBudgetDTO object
        return dto;
    }


}

