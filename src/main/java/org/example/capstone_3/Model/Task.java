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
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDateTime deadline;

    private String status;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "learning_group_id")
    private LearningGroup learningGroup;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<TaskSubmission> taskSubmissions;
}
