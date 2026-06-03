package org.example.capstone_3.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiResponse;
import org.example.capstone_3.DTO.IN.JobAnalysisDTOIn;
import org.example.capstone_3.Service.JobAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/job-analysis")
@RequiredArgsConstructor
public class JobAnalysisController {

    private final JobAnalysisService jobAnalysisService;

    @GetMapping("/get")
    public ResponseEntity<?> get() {
        return ResponseEntity.ok(jobAnalysisService.getAll());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getJobAnalysisById(@PathVariable Integer id) {
        return ResponseEntity.ok(jobAnalysisService.getById(id));
    }

    @PostMapping("/add/{studentId}")
    public ResponseEntity<?> saveJobAnalysis(@PathVariable Integer studentId,@RequestBody @Valid JobAnalysisDTOIn jobAnalysisDTOIn) {
        jobAnalysisService.addJobAnalysis(studentId, jobAnalysisDTOIn);
        return ResponseEntity.ok().body(new ApiResponse("Job analysis has been saved successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateJobAnalysis(@PathVariable Integer id, @RequestBody @Valid JobAnalysisDTOIn jobAnalysisDTOIn) {
        jobAnalysisService.updateJobAnalysis(id, jobAnalysisDTOIn);
        return ResponseEntity.ok().body(new ApiResponse("Job analysis has been updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteJobAnalysis(@PathVariable Integer id) {
        jobAnalysisService.delete(id);
        return ResponseEntity.ok().body(new ApiResponse("Job analysis has been deleted successfully"));
    }
}
