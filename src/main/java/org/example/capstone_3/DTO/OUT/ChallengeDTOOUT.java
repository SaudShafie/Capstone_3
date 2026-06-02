package org.example.capstone_3.DTO.OUT;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeDTOOUT {

    private Integer id;
    private String title;
    private String question;
    private String correctAnswer;
    private Integer points;
    private String difficulty;
    private Integer skillId;
}
