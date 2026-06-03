package org.example.capstone_3.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiResponse;
import org.example.capstone_3.DTO.IN.ReviewDTOIN;
import org.example.capstone_3.Service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/get")
    public ResponseEntity<?> get() {
        return ResponseEntity.ok(reviewService.getAll());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getReviewById(@PathVariable Integer id) {
        return ResponseEntity.ok(reviewService.getById(id));
    }

    @GetMapping("/mentor/{mentorId}")
    public ResponseEntity<?> getReviewsByMentorId(@PathVariable Integer mentorId) {
        return ResponseEntity.ok(reviewService.getReviewsByMentorId(mentorId));
    }

    @PostMapping("/add/{studentId}/{mentorId}/{mockInterviewId}")
    public ResponseEntity<?> saveReview(@PathVariable Integer studentId,
                                        @PathVariable Integer mentorId,
                                        @PathVariable Integer mockInterviewId,
                                        @RequestBody @Valid ReviewDTOIN reviewDTOIN) {
        reviewService.create(studentId, mentorId, mockInterviewId, reviewDTOIN);
        return ResponseEntity.ok().body(new ApiResponse("Review has been saved successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateReview(@PathVariable Integer id,
                                          @RequestBody @Valid ReviewDTOIN reviewDTOIN) {
        reviewService.update(id, reviewDTOIN);
        return ResponseEntity.ok().body(new ApiResponse("Review has been updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Integer id) {
        reviewService.delete(id);
        return ResponseEntity.ok().body(new ApiResponse("Review has been deleted successfully"));
    }
}