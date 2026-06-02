package org.example.capstone_3.DTO.IN;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JobAnalysisReportDTOIn {

    @NotEmpty(message = "Summary is required")
    private String summary;

    private String improvements;

    private String recommendations;
}