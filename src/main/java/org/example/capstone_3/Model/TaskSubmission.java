package org.example.capstone_3.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Check(constraints = "status='SUBMITTED' or status='NOT SUBMITTED' or status='EVALUATED'")
public class TaskSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String answerText;

    @Column(columnDefinition = "varchar(80) not null")
    private String status;

    @Column(columnDefinition = "int not null")
    private Integer score;

    @Column(columnDefinition = "datetime not null")
    private LocalDateTime submittedAt;

    @Column(columnDefinition = "TEXT")
    private String aiFeedback;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
}
