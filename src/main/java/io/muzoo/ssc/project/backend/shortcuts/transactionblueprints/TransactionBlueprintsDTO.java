package io.muzoo.ssc.project.backend.shortcuts.transactionblueprints;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TransactionBlueprintsDTO {

    private long id;
    private long userId;
    private long tagId;
    private long tagId2;
    private String transactionType; // Assuming Type is an enum with values like INCOME, EXPENDITURE
    private String shortcutType; // Assuming Type is an enum with values like FAVORITES, RECURRING
    private String notes;
    private BigDecimal value;
    
}
