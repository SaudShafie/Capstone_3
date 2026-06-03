package org.example.capstone_3.DTO.OUT;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JobAnalysisReportDTOOut {

    private Integer id;

    private String summary;

    private String improvements;

    private String recommendations;

    private StudentSummaryDTOOut student;

    private JobAnalysisSummaryDTOOut jobAnalysis;
}