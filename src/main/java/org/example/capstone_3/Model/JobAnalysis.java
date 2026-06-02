package org.example.capstone_3.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String jobTitle;

    @Column(columnDefinition = "TEXT")
    private String jobDescription;

    @Column(columnDefinition = "TEXT")
    private String requiredSkillsText;

    @Column(columnDefinition = "TEXT")
    private String missingSkillsText;

    private Integer matchScore;

    @Column(columnDefinition = "TEXT")
    private String recommendations;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToMany
    @JoinTable(
            name = "job_analysis_skills",
            joinColumns = @JoinColumn(name = "job_analysis_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills;

    @OneToOne(mappedBy = "jobAnalysis", cascade = CascadeType.ALL)
    @JsonIgnore
    private JobAnalysisReport jobAnalysisReport;

    @OneToOne(mappedBy = "jobAnalysis", cascade = CascadeType.ALL)
    @JsonIgnore
    private Roadmap roadmap;

    @OneToMany(mappedBy = "jobAnalysis", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<MockInterview> mockInterviews;
}