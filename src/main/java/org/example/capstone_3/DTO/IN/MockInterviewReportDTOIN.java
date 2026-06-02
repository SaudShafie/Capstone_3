package org.example.capstone_3.DTO.IN;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MockInterviewReportDTOIN {

    @NotBlank(message = "Report summary is required")
    private String summary;

    @NotBlank(message = "Strengths are required")
    private String strengths;

    @NotBlank(message = "Weaknesses are required")
    private String weaknesses;

    @NotBlank(message = "Recommendations are required")
    private String recommendations;

    @NotNull(message = "Generated date and time are required")
    private LocalDateTime generatedAt;

    @NotNull(message = "Student id is required")
    @Min(value = 1, message = "Student id must be a positive number")
    private Integer studentId;

    @NotNull(message = "Mock interview id is required")
    @Min(value = 1, message = "Mock interview id must be a positive number")
    private Integer mockInterviewId;
}
