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
public class Mentor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String fullName;

    @Column(unique = true)
    private String email;

    private String password;

    private String jobTitle;

    private String company;

    private String specialization;

    private Integer yearsExperience;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private Boolean volunteer;

    private Double sessionPrice;

    private Double rating;

    private Boolean available;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Review> reviews;

    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<MockInterview> mockInterviews;
}