package org.example.capstone_3.Controller;

import org.example.capstone_3.DTO.IN.MentorDTOIn;
import org.example.capstone_3.Service.MentorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/mentors")
@RestController
@RequiredArgsConstructor
public class MentorController {

    private final MentorService mentorService;

    @PostMapping
    public ResponseEntity<?> addMentor(@RequestBody @Valid MentorDTOIn mentorDTOIn) {
        mentorService.addMentor(mentorDTOIn);
        return ResponseEntity.status(201).body("Mentor added successfully");
    }

    @GetMapping
    public ResponseEntity<?> getAllMentors() {
        return ResponseEntity.status(200).body(mentorService.getAllMentors());
    }

    @GetMapping("/{mentorId}")
    public ResponseEntity<?> getMentorById(@PathVariable Integer mentorId) {
        return ResponseEntity.status(200).body(mentorService.getMentorById(mentorId));
    }

    @PutMapping("/{mentorId}")
    public ResponseEntity<?> updateMentor(@PathVariable Integer mentorId,
                                          @RequestBody @Valid MentorDTOIn mentorDTOIn) {
        mentorService.updateMentor(mentorId, mentorDTOIn);
        return ResponseEntity.status(200).body("Mentor updated successfully");
    }

    @DeleteMapping("/{mentorId}")
    public ResponseEntity<?> deleteMentor(@PathVariable Integer mentorId) {
        mentorService.deleteMentor(mentorId);
        return ResponseEntity.status(200).body("Mentor deleted successfully");
    }
}