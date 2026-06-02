package org.example.capstone_3.DTO.IN;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SkillDTOIn {

    @NotEmpty(message = "Skill name is required")
    private String name;

    @NotEmpty(message = "Category is required")
    private String category;
}