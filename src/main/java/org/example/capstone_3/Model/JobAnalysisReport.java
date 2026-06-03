package org.example.capstone_3.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobAnalysisReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "text not null")
    private String summary;

    @Column(columnDefinition = "text not null")
    private String improvements;

    @Column(columnDefinition = "text not null")
    private String recommendations;

    @Column(updatable = false, columnDefinition = "datetime not null")
    private LocalDateTime generatedAt;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @OneToOne
    @JoinColumn(name = "job_analysis_id", unique = true)
    private JobAnalysis jobAnalysis;
}