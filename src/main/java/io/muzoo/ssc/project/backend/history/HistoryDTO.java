package io.muzoo.ssc.project.backend.history;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.muzoo.ssc.project.backend.Transaction.Transaction;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class HistoryDTO {
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<Transaction> transactions;

    private boolean loggedIn = false;
}
