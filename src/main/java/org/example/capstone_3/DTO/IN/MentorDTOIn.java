package org.example.capstone_3.DTO.IN;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MentorDTOIn {

    @NotEmpty(message = "Full name is required")
    private String fullName;

    @Email(message = "Email must be valid")
    @NotEmpty(message = "Email is required")
    private String email;

    @NotEmpty(message = "Password is required")
    private String password;

    @NotEmpty(message = "Job title is required")
    private String jobTitle;

    @NotEmpty(message = "Company is required")
    private String company;

    @NotEmpty(message = "Specialization is required")
    private String specialization;

    @NotNull(message = "Years experience is required")
    @Min(value = 0, message = "Years experience must be 0 or more")
    private Integer yearsExperience;

    private String bio;

    @NotNull(message = "Volunteer status is required")
    private Boolean volunteer;

    private Double sessionPrice;
}