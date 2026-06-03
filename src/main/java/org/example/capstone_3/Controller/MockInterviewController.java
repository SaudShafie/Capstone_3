package org.example.capstone_3.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiResponse;
import org.example.capstone_3.DTO.IN.MockInterviewDTOIN;
import org.example.capstone_3.Service.MockInterviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mock-interview")
@RequiredArgsConstructor
public class MockInterviewController {

    private final MockInterviewService mockInterviewService;

    @GetMapping("/get")
    public ResponseEntity<?> get() {
        return ResponseEntity.ok(mockInterviewService.getAll());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getMockInterviewById(@PathVariable Integer id) {
        return ResponseEntity.ok(mockInterviewService.getById(id));
    }

    @GetMapping("/pending/{mentorId}")
    public ResponseEntity<?> getPendingMockInterviews(@PathVariable Integer mentorId) {
        return ResponseEntity.ok(mockInterviewService.getPendingMockInterviews(mentorId));
    }

    @PostMapping("/add/{studentId}/{mentorId}")
    public ResponseEntity<?> saveMockInterview(@PathVariable Integer studentId,
                                               @PathVariable Integer mentorId,
                                               @RequestBody @Valid MockInterviewDTOIN mockInterviewDTOIN) {
        mockInterviewService.create(studentId, mentorId, mockInterviewDTOIN);
        return ResponseEntity.ok().body(new ApiResponse("Mock interview has been saved successfully"));
    }

    @PostMapping("/add/{studentId}/{mentorId}/{jobAnalysisId}")
    public ResponseEntity<?> saveMockInterviewWithJobAnalysis(@PathVariable Integer studentId,
                                                              @PathVariable Integer mentorId,
                                                              @PathVariable Integer jobAnalysisId,
                                                              @RequestBody @Valid MockInterviewDTOIN mockInterviewDTOIN) {
        mockInterviewService.createWithJobAnalysis(studentId, mentorId, jobAnalysisId, mockInterviewDTOIN);
        return ResponseEntity.ok().body(new ApiResponse("Mock interview has been saved successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMockInterview(@PathVariable Integer id,
                                                 @RequestBody @Valid MockInterviewDTOIN mockInterviewDTOIN) {
        mockInterviewService.update(id, mockInterviewDTOIN);
        return ResponseEntity.ok().body(new ApiResponse("Mock interview has been updated successfully"));
    }

    @PutMapping("/accept/{mentorId}/{mockInterviewId}")
    public ResponseEntity<?> acceptMockInterview(@PathVariable Integer mentorId,
                                                 @PathVariable Integer mockInterviewId) {
        mockInterviewService.acceptMockInterview(mentorId, mockInterviewId);
        return ResponseEntity.ok().body(new ApiResponse("Mock interview has been accepted successfully"));
    }

    @PutMapping("/reject/{mentorId}/{mockInterviewId}")
    public ResponseEntity<?> rejectMockInterview(@PathVariable Integer mentorId,
                                                 @PathVariable Integer mockInterviewId) {
        mockInterviewService.rejectMockInterview(mentorId, mockInterviewId);
        return ResponseEntity.ok().body(new ApiResponse("Mock interview has been rejected successfully"));
    }

    @PutMapping("/complete/{mentorId}/{mockInterviewId}")
    public ResponseEntity<?> completeMockInterview(@PathVariable Integer mentorId,
                                                   @PathVariable Integer mockInterviewId,
                                                   @RequestParam String feedback,
                                                   @RequestParam Integer score) {
        mockInterviewService.completeMockInterview(mentorId, mockInterviewId, feedback, score);
        return ResponseEntity.ok().body(new ApiResponse("Mock interview has been completed successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMockInterview(@PathVariable Integer id) {
        mockInterviewService.delete(id);
        return ResponseEntity.ok().body(new ApiResponse("Mock interview has been deleted successfully"));
    }
}