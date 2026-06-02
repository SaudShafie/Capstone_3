package org.example.capstone_3.DTO.OUT;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AdminDTOOut {

    private Integer id;

    private String fullName;

    private String email;

}