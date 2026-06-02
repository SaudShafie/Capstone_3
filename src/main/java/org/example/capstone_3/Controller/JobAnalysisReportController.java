package org.example.capstone_3.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiResponse;
import org.example.capstone_3.DTO.IN.JobAnalysisReportDTOIn;
import org.example.capstone_3.Service.JobAnalysisReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/job-analysis-report")
@RequiredArgsConstructor
public class JobAnalysisReportController {

    private final JobAnalysisReportService jobAnalysisReportService;

    @GetMapping("/get")
    public ResponseEntity<?> get() {
        return ResponseEntity.ok(jobAnalysisReportService.getAll());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getJobAnalysisReportById(@PathVariable Integer id) {
        return ResponseEntity.ok(jobAnalysisReportService.getById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<?> saveJobAnalysisReport(@RequestBody @Valid JobAnalysisReportDTOIn jobAnalysisReportDTOIn) {
        jobAnalysisReportService.create(jobAnalysisReportDTOIn);
        return ResponseEntity.ok().body(new ApiResponse("Job analysis report has been saved successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateJobAnalysisReport(@PathVariable Integer id, @RequestBody @Valid JobAnalysisReportDTOIn jobAnalysisReportDTOIn) {
        jobAnalysisReportService.update(id, jobAnalysisReportDTOIn);
        return ResponseEntity.ok().body(new ApiResponse("Job analysis report has been updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteJobAnalysisReport(@PathVariable Integer id) {
        jobAnalysisReportService.delete(id);
        return ResponseEntity.ok().body(new ApiResponse("Job analysis report has been deleted successfully"));
    }
}
