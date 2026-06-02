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
public class Roadmap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    private String targetRole;

    private Integer progressPercentage;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @OneToOne
    @JoinColumn(name = "job_analysis_id")
    private JobAnalysis jobAnalysis;

    @OneToMany(mappedBy = "roadmap", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<RoadmapStep> roadmapSteps;
}