package org.example.capstone_3.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "challenge_attempts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String submittedAnswer;

    private Boolean correct;

    private Integer earnedPoints;

    private LocalDateTime submittedAt;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

}
