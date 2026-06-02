package org.example.capstone_3.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiResponse;
import org.example.capstone_3.DTO.IN.LearningGroupDTOIN;
import org.example.capstone_3.Service.LearningGroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/learning-group")
@RequiredArgsConstructor
public class LearningGroupController {
    private final LearningGroupService learningGroupService;

    @GetMapping("/get")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(learningGroupService.getAllLearningGroups());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(learningGroupService.getLearningGroupById(id));
    }

    @PostMapping("/add/{studentId}")
    public ResponseEntity<?> add(@PathVariable Integer studentId, @RequestBody @Valid LearningGroupDTOIN dto) {
        learningGroupService.addLearningGroup(studentId, dto);
        return ResponseEntity.ok(new ApiResponse("Learning group created successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody @Valid LearningGroupDTOIN dto) {
        learningGroupService.updateLearningGroup(id, dto);
        return ResponseEntity.ok(new ApiResponse("Learning group updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        learningGroupService.deleteLearningGroup(id);
        return ResponseEntity.ok(new ApiResponse("Learning group deleted successfully"));
    }
}
