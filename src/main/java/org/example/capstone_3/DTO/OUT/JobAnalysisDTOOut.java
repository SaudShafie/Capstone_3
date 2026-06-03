package org.example.capstone_3.DTO.OUT;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

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

    private StudentSummaryDTOOut student;

    private Set<SkillDTOOut> skills;
}