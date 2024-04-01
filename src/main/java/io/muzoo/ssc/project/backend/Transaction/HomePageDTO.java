package io.muzoo.ssc.project.backend.Transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Include non-empty to avoid null fields
public class HomePageDTO {
    private boolean success;
    private String message;
    private List<?> data;
    private BigDecimal totalIncome;
    private BigDecimal totalExpenditure;
    private BigDecimal balance;
}
