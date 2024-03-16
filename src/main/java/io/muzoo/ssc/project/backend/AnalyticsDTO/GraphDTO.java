package io.muzoo.ssc.project.backend.AnalyticsDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GraphDTO {
    private List<Object[]> data;
    private boolean tagged;
}
