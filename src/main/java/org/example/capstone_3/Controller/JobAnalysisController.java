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

<<<<<<< HEAD
=======
//    @GetMapping("/get")
//    public ResponseEntity<?> get() {
//        return ResponseEntity.ok(jobAnalysisService.getAll());
//    }


>>>>>>> cd2506de3f0b6197e5d69249f722a5ed8af607e6
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getJobAnalysisById(@PathVariable Integer id) {
        return ResponseEntity.ok(jobAnalysisService.getById(id));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getJobAnalysesByStudentId(@PathVariable Integer studentId) {
        return ResponseEntity.ok(jobAnalysisService.getByStudentId(studentId));

    }

<<<<<<< HEAD
    @PostMapping("/add/{studentId}")
    public ResponseEntity<?> saveJobAnalysis(
            @PathVariable Integer studentId,
            @RequestBody @Valid JobAnalysisDTOIn jobAnalysisDTOIn) {
        return ResponseEntity.ok(jobAnalysisService.addJobAnalysis(studentId, jobAnalysisDTOIn));

    }

=======
>>>>>>> cd2506de3f0b6197e5d69249f722a5ed8af607e6


    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateJobAnalysis(
            @PathVariable Integer id,
            @RequestBody @Valid JobAnalysisDTOIn jobAnalysisDTOIn) {
        return ResponseEntity.ok(jobAnalysisService.updateJobAnalysis(id, jobAnalysisDTOIn));

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteJobAnalysis(@PathVariable Integer id) {
        jobAnalysisService.delete(id);
        return ResponseEntity.ok().body(new ApiResponse("Job analysis has been deleted successfully"));
    }

}
<<<<<<< HEAD


=======
>>>>>>> cd2506de3f0b6197e5d69249f722a5ed8af607e6
