package org.example.capstone_3.Controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.capstone_3.Api.ApiResponse;
import org.example.capstone_3.DTO.IN.MockInterviewDTOIN;
import org.example.capstone_3.Service.MockInterviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mock-interview")
@AllArgsConstructor
public class MockInterviewController {

    private MockInterviewService mockInterviewService;

    @GetMapping("/get")
    public ResponseEntity<?> get() {
        return ResponseEntity.ok(mockInterviewService.getAll());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getMockInterviewById(@PathVariable Integer id) {
        return ResponseEntity.ok(mockInterviewService.getById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<?> saveMockInterview(@RequestBody @Valid MockInterviewDTOIN mockInterviewDTOIN) {
        mockInterviewService.create(mockInterviewDTOIN);
        return ResponseEntity.ok().body(new ApiResponse("Mock interview has been saved successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMockInterview(@PathVariable Integer id, @RequestBody @Valid MockInterviewDTOIN mockInterviewDTOIN) {
        mockInterviewService.update(id, mockInterviewDTOIN);
        return ResponseEntity.ok().body(new ApiResponse("Mock interview has been updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMockInterview(@PathVariable Integer id) {
        mockInterviewService.delete(id);
        return ResponseEntity.ok().body(new ApiResponse("Mock interview has been deleted successfully"));
    }
}
