package org.example.capstone_3.DTO.OUT;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoadmapDTOOUT {
    private Integer id;

    private String title;

    private String targetRole;

    private Integer progressPercentage;

    private LocalDateTime createdAt;

    private Integer studentId;

    private Integer jobAnalysisId;

    private List<RoadmapStepDTOOUT> roadmapSteps;
}
