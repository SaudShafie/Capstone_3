package org.example.capstone_3.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiResponse;
import org.example.capstone_3.DTO.IN.TaskSubmissionDTOIN;
import org.example.capstone_3.Service.TaskSubmissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/task-submission")
@RequiredArgsConstructor
public class TaskSubmissionController {
    private final TaskSubmissionService taskSubmissionService;

    @GetMapping("/get")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(taskSubmissionService.getAllTaskSubmissions());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(taskSubmissionService.getTaskSubmissionById(id));
    }

    @PostMapping("/add/{taskId}/{studentId}")
    public ResponseEntity<?> add(@PathVariable Integer taskId, @PathVariable Integer studentId, @RequestBody @Valid TaskSubmissionDTOIN dto) {
        taskSubmissionService.addTaskSubmission(taskId, studentId, dto);
        return ResponseEntity.ok(new ApiResponse("Submission created successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody @Valid TaskSubmissionDTOIN dto) {
        taskSubmissionService.updateTaskSubmission(id, dto);
        return ResponseEntity.ok(new ApiResponse("Submission updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        taskSubmissionService.deleteTaskSubmission(id);
        return ResponseEntity.ok(new ApiResponse("Submission deleted successfully"));
    }
}
