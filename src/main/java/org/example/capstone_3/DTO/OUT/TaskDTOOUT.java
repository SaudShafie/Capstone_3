package org.example.capstone_3.DTO.OUT;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTOOUT {
    private Integer id;

    private String title;

    private String description;

    private LocalDateTime deadline;

    private String status;

    private LocalDateTime createdAt;
}
