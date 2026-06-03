package org.example.capstone_3.DTO.IN;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MockInterviewDTOIN {

    @NotBlank(message = "Interview type is required")
    private String interviewType;

    @NotBlank(message = "Interview description is required")
    private String description;

    @NotNull(message = "Scheduled date and time are required")
    @Future(message = "Scheduled date must be in the future")
    private LocalDateTime scheduledAt;

    private String url;
}