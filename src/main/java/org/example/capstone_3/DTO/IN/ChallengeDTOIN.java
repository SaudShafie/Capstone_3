package org.example.capstone_3.DTO.IN;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChallengeDTOIN {

    @NotBlank(message = "Challenge title is required")
    private String title;

    @NotBlank(message = "Challenge question is required")
    private String question;

    @NotBlank(message = "Correct answer is required")
    private String correctAnswer;

    @NotNull(message = "Points are required")
    @Min(value = 1, message = "Points must be at least 1")
    private Integer points;

    @NotBlank(message = "Difficulty is required")
    private String difficulty;

    @NotNull(message = "Skill id is required")
    @Min(value = 1, message = "Skill id must be a positive number")
    private Integer skillId;
}
