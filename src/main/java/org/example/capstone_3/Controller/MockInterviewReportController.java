package org.example.capstone_3.Controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.capstone_3.Api.ApiResponse;
import org.example.capstone_3.DTO.IN.MockInterviewReportDTOIN;
import org.example.capstone_3.Service.MockInterviewReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mock-interview-report")
@AllArgsConstructor
public class MockInterviewReportController {

    private MockInterviewReportService mockInterviewReportService;

    @GetMapping("/get")
    public ResponseEntity<?> get() {
        return ResponseEntity.ok(mockInterviewReportService.getAll());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getMockInterviewReportById(@PathVariable Integer id) {
        return ResponseEntity.ok(mockInterviewReportService.getById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<?> saveMockInterviewReport(@RequestBody @Valid MockInterviewReportDTOIN mockInterviewReportDTOIN) {
        mockInterviewReportService.create(mockInterviewReportDTOIN);
        return ResponseEntity.ok().body(new ApiResponse("Mock interview report has been saved successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMockInterviewReport(@PathVariable Integer id, @RequestBody @Valid MockInterviewReportDTOIN mockInterviewReportDTOIN) {
        mockInterviewReportService.update(id, mockInterviewReportDTOIN);
        return ResponseEntity.ok().body(new ApiResponse("Mock interview report has been updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMockInterviewReport(@PathVariable Integer id) {
        mockInterviewReportService.delete(id);
        return ResponseEntity.ok().body(new ApiResponse("Mock interview report has been deleted successfully"));
    }
}
