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
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String fullName;

    @Column(unique = true)
    private String email;

    private String password;

    private String major;

    private String targetRole;

    private Integer yearsExperience;

    private String linkedinUrl;

    private String githubUrl;

    @Column(columnDefinition = "TEXT")
    private String cvText;

    private Integer xp;

    private Integer readinessScore;

    private LocalDateTime createdAt;

    @ManyToMany
    @JoinTable(
            name = "student_skills",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills;

    @ManyToMany
    @JoinTable(
            name = "student_learning_groups",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "learning_group_id")
    )
    private Set<LearningGroup> learningGroups;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<TaskSubmission> taskSubmissions;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Review> reviews;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<JobAnalysis> jobAnalyses;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<JobAnalysisReport> jobAnalysisReports;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Roadmap> roadmaps;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ChallengeAttempt> challengeAttempts;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<MockInterview> mockInterviews;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<MockInterviewReport> mockInterviewReports;
}