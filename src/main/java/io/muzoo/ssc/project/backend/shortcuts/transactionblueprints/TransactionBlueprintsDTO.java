package io.muzoo.ssc.project.backend.shortcuts.transactionblueprints;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TransactionBlueprintsDTO {

    private List<TransactionBlueprints> transactionBlueprintsList;
    
}
