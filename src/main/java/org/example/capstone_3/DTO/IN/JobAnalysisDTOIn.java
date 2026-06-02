package org.example.capstone_3.DTO.IN;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JobAnalysisDTOIn {

    @NotEmpty(message = "Job title is required")
    private String jobTitle;

    @NotEmpty(message = "Job description is required")
    private String jobDescription;

    private String requiredSkillsText;

    private Integer studentId;
}