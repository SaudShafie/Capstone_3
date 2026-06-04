package org.example.capstone_3.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiResponse;
import org.example.capstone_3.DTO.IN.AiInterviewAnswerDTOIN;
import org.example.capstone_3.DTO.IN.AiMockInterviewDTOIN;
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

    // =========================
    // MENTOR FLOW
    // =========================

    @GetMapping("/mentor/pending/{mentorId}")
    public ResponseEntity<?> getPendingMentorInterviews(@PathVariable Integer mentorId) {
        return ResponseEntity.ok(mockInterviewService.getPendingMentorInterviews(mentorId));
    }

    @PostMapping("/mentor/add/{studentId}/{mentorId}")
    public ResponseEntity<?> createMentorInterview(@PathVariable Integer studentId,
                                                   @PathVariable Integer mentorId,
                                                   @RequestBody @Valid MockInterviewDTOIN dto) {
        mockInterviewService.createMentorInterview(studentId, mentorId, dto);
        return ResponseEntity.ok().body(new ApiResponse("Mentor mock interview request has been created successfully"));
    }

    @PutMapping("/mentor/accept/{mentorId}/{mockInterviewId}")
    public ResponseEntity<?> acceptMentorInterview(@PathVariable Integer mentorId,
                                                   @PathVariable Integer mockInterviewId) {
        mockInterviewService.acceptMentorInterview(mentorId, mockInterviewId);
        return ResponseEntity.ok().body(new ApiResponse("Mock interview has been scheduled successfully"));
    }
//
    @PutMapping("/mentor/complete/{mentorId}/{mockInterviewId}")
    public ResponseEntity<?> completeMentorInterview(@PathVariable Integer mentorId,
                                                     @PathVariable Integer mockInterviewId,
                                                     @RequestParam String feedback,
                                                     @RequestParam Integer score) {
        mockInterviewService.completeMentorInterview(mentorId, mockInterviewId, feedback, score);
        return ResponseEntity.ok().body(new ApiResponse("Mock interview has been completed successfully"));
    }

    // =========================
    // AI FLOW
    // =========================

    @PostMapping("/ai/add/{studentId}")
    public ResponseEntity<?> createAiInterview(@PathVariable Integer studentId,
                                               @RequestBody @Valid AiMockInterviewDTOIN dto) {
        mockInterviewService.createAiInterview(studentId, dto);
        return ResponseEntity.ok().body(new ApiResponse("AI mock interview has been created successfully"));
    }

    @PutMapping("/ai/submit/{studentId}/{mockInterviewId}")
    public ResponseEntity<?> submitAiInterviewAnswers(@PathVariable Integer studentId,
                                                      @PathVariable Integer mockInterviewId,
                                                      @RequestBody @Valid AiInterviewAnswerDTOIN dto) {
        mockInterviewService.submitAiInterviewAnswers(studentId, mockInterviewId, dto);
        return ResponseEntity.ok().body(new ApiResponse("AI mock interview answers have been evaluated successfully"));
    }

    @GetMapping("/ai/student/{studentId}")
    public ResponseEntity<?> getStudentAiInterviews(@PathVariable Integer studentId) {
        return ResponseEntity.ok(mockInterviewService.getStudentAiInterviews(studentId));
    }

    // =========================
    // DELETE
    // =========================

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMockInterview(@PathVariable Integer id) {
        mockInterviewService.delete(id);
        return ResponseEntity.ok().body(new ApiResponse("Mock interview has been deleted successfully"));
    }
}