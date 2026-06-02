package org.example.capstone_3.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "mock_interviews")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MockInterview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String interviewType;

    private LocalDateTime scheduledAt;

    private String status;

    @Column(columnDefinition = "TEXT")
    private String questions;

    @Column(columnDefinition = "TEXT")
    private String studentAnswers;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    private Integer score;

    private String url;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "mentor_id")
    private Mentor mentor;

    @ManyToOne
    @JoinColumn(name = "job_analysis_id")
    private JobAnalysis jobAnalysis;

    @OneToOne(mappedBy = "mockInterview", cascade = CascadeType.ALL)
    @JsonIgnore
    private MockInterviewReport mockInterviewReport;
}
