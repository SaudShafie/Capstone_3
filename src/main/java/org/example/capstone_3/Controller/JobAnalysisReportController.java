package org.example.capstone_3.Controller;

import org.example.capstone_3.DTO.IN.JobAnalysisReportDTOIn;
import org.example.capstone_3.Service.JobAnalysisReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/job-analysis-reports")
@RestController
@RequiredArgsConstructor
public class JobAnalysisReportController {

    private final JobAnalysisReportService jobAnalysisReportService;

    @PostMapping("/student/{studentId}/job-analysis/{jobAnalysisId}")
    public ResponseEntity<?> addJobAnalysisReport(@PathVariable Integer studentId,
                                                  @PathVariable Integer jobAnalysisId,
                                                  @RequestBody @Valid JobAnalysisReportDTOIn jobAnalysisReportDTOIn) {
        jobAnalysisReportService.addJobAnalysisReport(studentId, jobAnalysisId, jobAnalysisReportDTOIn);
        return ResponseEntity.status(201).body("Job analysis report added successfully");
    }

    @GetMapping
    public ResponseEntity<?> getAllJobAnalysisReports() {
        return ResponseEntity.status(200).body(jobAnalysisReportService.getAllJobAnalysisReports());
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<?> getJobAnalysisReportById(@PathVariable Integer reportId) {
        return ResponseEntity.status(200).body(jobAnalysisReportService.getJobAnalysisReportById(reportId));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getReportsByStudentId(@PathVariable Integer studentId) {
        return ResponseEntity.status(200).body(jobAnalysisReportService.getReportsByStudentId(studentId));
    }

    @GetMapping("/job-analysis/{jobAnalysisId}")
    public ResponseEntity<?> getReportByJobAnalysisId(@PathVariable Integer jobAnalysisId) {
        return ResponseEntity.status(200).body(jobAnalysisReportService.getReportByJobAnalysisId(jobAnalysisId));
    }

    @PutMapping("/{reportId}")
    public ResponseEntity<?> updateJobAnalysisReport(@PathVariable Integer reportId,
                                                     @RequestBody @Valid JobAnalysisReportDTOIn jobAnalysisReportDTOIn) {
        jobAnalysisReportService.updateJobAnalysisReport(reportId, jobAnalysisReportDTOIn);
        return ResponseEntity.status(200).body("Job analysis report updated successfully");
    }

    @DeleteMapping("/{reportId}")
    public ResponseEntity<?> deleteJobAnalysisReport(@PathVariable Integer reportId) {
        jobAnalysisReportService.deleteJobAnalysisReport(reportId);
        return ResponseEntity.status(200).body("Job analysis report deleted successfully");
    }
}