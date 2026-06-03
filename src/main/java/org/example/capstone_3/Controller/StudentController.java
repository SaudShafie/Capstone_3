package org.example.capstone_3.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiResponse;
import org.example.capstone_3.DTO.IN.StudentDTOIn;
import org.example.capstone_3.Service.RoadmapService;
import org.example.capstone_3.Service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final RoadmapService roadmapService;

    @GetMapping("/get")
    public ResponseEntity<?> get() {
        return ResponseEntity.ok(studentService.getAll());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Integer id) {
        return ResponseEntity.ok(studentService.getById(id));
    }

    @GetMapping("/get/{id}/roadmaps")
    public ResponseEntity<?> getStudentRoadmaps(@PathVariable Integer id) {
        return ResponseEntity.ok(roadmapService.getRoadmapsByStudentId(id));
    }

    @PostMapping("/add")
    public ResponseEntity<?> saveStudent(@RequestBody @Valid StudentDTOIn studentDTOIn) {
        studentService.addStudent(studentDTOIn);
        return ResponseEntity.ok().body(new ApiResponse("Student has been saved successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable Integer id, @RequestBody @Valid StudentDTOIn studentDTOIn) {
        studentService.updateStudent(id, studentDTOIn);
        return ResponseEntity.ok().body(new ApiResponse("Student has been updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Integer id) {
        studentService.delete(id);
        return ResponseEntity.ok().body(new ApiResponse("Student has been deleted successfully"));
    }

    @PostMapping("/add-skill/{studentId}/{skillId}")
    public ResponseEntity<?> addSkillToStudent(
            @PathVariable Integer studentId,
            @PathVariable Integer skillId) {
        studentService.addSkillToStudent(studentId, skillId);
        return ResponseEntity.ok().body(new ApiResponse("Skill has been added to student successfully"));
    }

    @DeleteMapping("/remove-skill/{studentId}/{skillId}")
    public ResponseEntity<?> removeSkillFromStudent(
            @PathVariable Integer studentId,
            @PathVariable Integer skillId) {
        studentService.removeSkillFromStudent(studentId, skillId);
        return ResponseEntity.ok().body(new ApiResponse("Skill has been removed from student successfully"));
    }
}
