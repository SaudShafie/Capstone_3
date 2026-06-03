package org.example.capstone_3.DTO.OUT;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MentorDTOOut {

    private Integer id;

    private String fullName;

    private String email;

    private String jobTitle;

    private String company;

    private String specialization;

    private Integer yearsExperience;

    private String bio;

    private Boolean volunteer;

    private Double sessionPrice;

    private Double rating;

    private Boolean available;

    private Boolean acceptedByAdmin;
}