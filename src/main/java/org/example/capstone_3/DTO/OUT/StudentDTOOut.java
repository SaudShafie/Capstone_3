package org.example.capstone_3.DTO.OUT;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class StudentDTOOut {

    private Integer id;

    private String fullName;

    private String email;

    private String major;

    private String targetRole;

    private Integer yearsExperience;

    private String linkedinUrl;

    private String githubUrl;

    private String cvText;

    private Integer xp;

    private Integer readinessScore;

    private Set<SkillDTOOut> skills;
}