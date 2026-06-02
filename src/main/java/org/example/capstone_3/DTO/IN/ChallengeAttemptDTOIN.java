package org.example.capstone_3.DTO.IN;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ChallengeAttemptDTOIN {

    @NotBlank(message = "Submitted answer is required")
    private String submittedAnswer;

    @NotNull(message = "Correct flag is required (true or false)")
    private Boolean correct;

    @NotNull(message = "Earned points are required")
    @Min(value = 0, message = "Earned points cannot be negative")
    private Integer earnedPoints;

    @NotNull(message = "Submission date and time are required")
    private LocalDateTime submittedAt;

    @NotNull(message = "Student id is required")
    @Min(value = 1, message = "Student id must be a positive number")
    private Integer studentId;

    @NotNull(message = "Challenge id is required")
    @Min(value = 1, message = "Challenge id must be a positive number")
    private Integer challengeId;
}
