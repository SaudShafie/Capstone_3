package org.example.capstone_3.Controller;

import org.example.capstone_3.DTO.IN.JobAnalysisDTOIn;
import org.example.capstone_3.Service.JobAnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/job-analyses")
@RestController
@RequiredArgsConstructor
public class JobAnalysisController {

    private final JobAnalysisService jobAnalysisService;

    @PostMapping("/student/{studentId}")
    public ResponseEntity<?> addJobAnalysis(@PathVariable Integer studentId,
                                            @RequestBody @Valid JobAnalysisDTOIn jobAnalysisDTOIn) {
        jobAnalysisService.addJobAnalysis(studentId, jobAnalysisDTOIn);
        return ResponseEntity.status(201).body("Job analysis added successfully");
    }

    @GetMapping
    public ResponseEntity<?> getAllJobAnalyses() {
        return ResponseEntity.status(200).body(jobAnalysisService.getAllJobAnalyses());
    }

    @GetMapping("/{jobAnalysisId}")
    public ResponseEntity<?> getJobAnalysisById(@PathVariable Integer jobAnalysisId) {
        return ResponseEntity.status(200).body(jobAnalysisService.getJobAnalysisById(jobAnalysisId));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getJobAnalysesByStudentId(@PathVariable Integer studentId) {
        return ResponseEntity.status(200).body(jobAnalysisService.getJobAnalysesByStudentId(studentId));
    }

    @PutMapping("/{jobAnalysisId}")
    public ResponseEntity<?> updateJobAnalysis(@PathVariable Integer jobAnalysisId,
                                               @RequestBody @Valid JobAnalysisDTOIn jobAnalysisDTOIn) {
        jobAnalysisService.updateJobAnalysis(jobAnalysisId, jobAnalysisDTOIn);
        return ResponseEntity.status(200).body("Job analysis updated successfully");
    }

    @DeleteMapping("/{jobAnalysisId}")
    public ResponseEntity<?> deleteJobAnalysis(@PathVariable Integer jobAnalysisId) {
        jobAnalysisService.deleteJobAnalysis(jobAnalysisId);
        return ResponseEntity.status(200).body("Job analysis deleted successfully");
    }
}