package org.example.capstone_3.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiResponse;
import org.example.capstone_3.DTO.IN.RoadmapDTOIN;
import org.example.capstone_3.Service.RoadmapService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/roadmap")
@RequiredArgsConstructor
public class RoadmapController {
    private final RoadmapService roadmapService;

    @GetMapping("/get")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(roadmapService.getAllRoadmaps());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(roadmapService.getRoadmapById(id));
    }

    @PostMapping("/add/{studentId}")
    public ResponseEntity<?> add(@PathVariable Integer studentId, @RequestBody @Valid RoadmapDTOIN dto) {
        roadmapService.addRoadmap(studentId, dto);
        return ResponseEntity.ok(new ApiResponse("Roadmap created successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody @Valid RoadmapDTOIN dto) {
        roadmapService.updateRoadmap(id, dto);
        return ResponseEntity.ok(new ApiResponse("Roadmap updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        roadmapService.deleteRoadmap(id);
        return ResponseEntity.ok(new ApiResponse("Roadmap deleted successfully"));
    }
}
