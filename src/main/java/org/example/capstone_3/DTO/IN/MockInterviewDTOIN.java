package org.example.capstone_3.DTO.IN;

import jakarta.validation.constraints.Min;
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

    @NotNull(message = "Scheduled date and time are required")
    private LocalDateTime scheduledAt;

    @NotBlank(message = "Interview status is required")
    private String status;


    private String url;

    @NotNull(message = "Created date and time are required")
    private LocalDateTime createdAt;

//    @NotNull(message = "Student id is required")
//    @Min(value = 1, message = "Student id must be a positive number")
//    private Integer studentId;
//
//    @NotNull(message = "Mentor id is required")
//    @Min(value = 1, message = "Mentor id must be a positive number")
//    private Integer mentorId;



}
