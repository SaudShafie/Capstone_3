package org.example.capstone_3.DTO.OUT;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class JobAnalysisDTOOut {

    private Integer id;

    private String jobTitle;

    private String jobDescription;

    private String requiredSkillsText;

    private String missingSkillsText;

    private Integer matchScore;

    private String recommendations;

    private Integer studentId;

    private String studentName;
}