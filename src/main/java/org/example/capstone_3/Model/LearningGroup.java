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
public class LearningGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String focusArea;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDateTime createdAt;

    @ManyToMany(mappedBy = "learningGroups")
    @JsonIgnore
    private Set<Student> students;

    @OneToMany(mappedBy = "learningGroup", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Task> tasks;
}