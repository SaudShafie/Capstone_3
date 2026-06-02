package org.example.capstone_3.Controller;

import org.example.capstone_3.DTO.IN.StudentDTOIn;
import org.example.capstone_3.Service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/students")
@RestController
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<?> addStudent(@RequestBody @Valid StudentDTOIn studentDTOIn) {
        studentService.addStudent(studentDTOIn);
        return ResponseEntity.status(201).body("Student added successfully");
    }

    @GetMapping
    public ResponseEntity<?> getAllStudents() {
        return ResponseEntity.status(200).body(studentService.getAllStudents());
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<?> getStudentById(@PathVariable Integer studentId) {
        return ResponseEntity.status(200).body(studentService.getStudentById(studentId));
    }

    @PutMapping("/{studentId}")
    public ResponseEntity<?> updateStudent(@PathVariable Integer studentId,
                                           @RequestBody @Valid StudentDTOIn studentDTOIn) {
        studentService.updateStudent(studentId, studentDTOIn);
        return ResponseEntity.status(200).body("Student updated successfully");
    }

    @DeleteMapping("/{studentId}")
    public ResponseEntity<?> deleteStudent(@PathVariable Integer studentId) {
        studentService.deleteStudent(studentId);
        return ResponseEntity.status(200).body("Student deleted successfully");
    }
}