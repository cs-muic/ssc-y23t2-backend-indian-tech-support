package io.muzoo.ssc.project.backend.SidebarDTO;


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
public class AmountDTO {
    private BigDecimal totalAmount;
    private boolean found;
}
