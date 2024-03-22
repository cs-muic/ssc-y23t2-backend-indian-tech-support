package io.muzoo.ssc.project.backend.AnalyticsDTO;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

// Assuming Lombok annotations are used for boilerplate code like getters and setters
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AmountDTO {
    private BigDecimal totalAmount;
    private boolean found;
}
