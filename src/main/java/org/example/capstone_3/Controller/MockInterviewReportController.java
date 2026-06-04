package org.example.capstone_3.Controller;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiResponse;
import org.example.capstone_3.Service.MockInterviewReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mock-interview-report")
@RequiredArgsConstructor
public class MockInterviewReportController {

    private final MockInterviewReportService mockInterviewReportService;

    @GetMapping("/get")
    public ResponseEntity<?> get() {
        return ResponseEntity.ok(mockInterviewReportService.getAll());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getMockInterviewReportById(@PathVariable Integer id) {
        return ResponseEntity.ok(mockInterviewReportService.getById(id));
    }
// m id
    @PostMapping("/add/{mockInterviewId}")
    public ResponseEntity<?> saveMockInterviewReport(@PathVariable Integer mockInterviewId) {
        mockInterviewReportService.create(mockInterviewId);
        return ResponseEntity.ok().body(new ApiResponse("Mock interview report has been generated successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMockInterviewReport(@PathVariable Integer id) {
        mockInterviewReportService.update(id);
        return ResponseEntity.ok().body(new ApiResponse("Mock interview report has been regenerated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMockInterviewReport(@PathVariable Integer id) {
        mockInterviewReportService.delete(id);
        return ResponseEntity.ok().body(new ApiResponse("Mock interview report has been deleted successfully"));
    }
}