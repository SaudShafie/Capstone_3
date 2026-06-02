package org.example.capstone_3.Controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.capstone_3.Api.ApiResponse;
import org.example.capstone_3.DTO.IN.ChallengeAttemptDTOIN;
import org.example.capstone_3.Service.ChallengeAttemptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/challenge-attempt")
@AllArgsConstructor
public class ChallengeAttemptController {

    private ChallengeAttemptService challengeAttemptService;

    @GetMapping("/get")
    public ResponseEntity<?> get() {
        return ResponseEntity.ok(challengeAttemptService.getAll());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getChallengeAttemptById(@PathVariable Integer id) {
        return ResponseEntity.ok(challengeAttemptService.getById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<?> saveChallengeAttempt(@RequestBody @Valid ChallengeAttemptDTOIN challengeAttemptDTOIN) {
        challengeAttemptService.create(challengeAttemptDTOIN);
        return ResponseEntity.ok().body(new ApiResponse("Challenge attempt has been saved successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateChallengeAttempt(@PathVariable Integer id, @RequestBody @Valid ChallengeAttemptDTOIN challengeAttemptDTOIN) {
        challengeAttemptService.update(id, challengeAttemptDTOIN);
        return ResponseEntity.ok().body(new ApiResponse("Challenge attempt has been updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteChallengeAttempt(@PathVariable Integer id) {
        challengeAttemptService.delete(id);
        return ResponseEntity.ok().body(new ApiResponse("Challenge attempt has been deleted successfully"));
    }
}
