package org.example.capstone_3.DTO.OUT;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class JobAnalysisReportDTOOut {

    private Integer id;

    private String summary;

    private String improvements;

    private String recommendations;

    private LocalDateTime generatedAt;

    private Integer studentId;

    private String studentName;

    private Integer jobAnalysisId;

    private String jobTitle;
}