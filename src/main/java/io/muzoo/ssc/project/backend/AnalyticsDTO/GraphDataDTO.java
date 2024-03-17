package io.muzoo.ssc.project.backend.AnalyticsDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GraphDataDTO {
    private List<Object[]> data;
    private boolean tagged;
    private boolean empty;
}
