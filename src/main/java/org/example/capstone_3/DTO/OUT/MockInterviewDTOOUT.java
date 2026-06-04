package org.example.capstone_3.DTO.OUT;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MockInterviewDTOOUT {

    private Integer id;
    private String interviewType;
    private LocalDateTime scheduledAt;
    private String status;
    private String questions;
    private String studentAnswers;
    private String feedback;
    private Integer score;
    private String url;
    private LocalDateTime createdAt;
    private Integer studentId;
    private Integer mentorId;
}
