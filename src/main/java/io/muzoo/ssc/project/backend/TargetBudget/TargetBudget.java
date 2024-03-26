package io.muzoo.ssc.project.backend.TargetBudget;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
//@Builder
@Table(name = "target_budget")
public class TargetBudget{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long userId;

    private BigDecimal target;

    private BigDecimal budget;
}

