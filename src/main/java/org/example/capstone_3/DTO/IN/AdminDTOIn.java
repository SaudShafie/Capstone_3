package org.example.capstone_3.DTO.IN;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminDTOIn {

    @NotEmpty(message = "Full name is required")
    private String fullName;

    @Email(message = "Email must be valid")
    @NotEmpty(message = "Email is required")
    private String email;

    @NotEmpty(message = "Password is required")
    private String password;
}