package io.muzoo.ssc.project.backend.Transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

// Assuming Lombok annotations are used for boilerplate code like getters and setters
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TransactionDTO {
    private long id;
    private long userId;
    private long tagId;
    private long tagId2;
    private String type; // Assuming Type is an enum with values like INCOME, EXPENDITURE
    private String notes;
    private BigDecimal value;
    private Timestamp timestamp;
}
