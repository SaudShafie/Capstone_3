package org.example.capstone_3.Controller;

import org.example.capstone_3.DTO.IN.SkillDTOIn;
import org.example.capstone_3.Service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/skills")
@RestController
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @PostMapping
    public ResponseEntity<?> addSkill(@RequestBody @Valid SkillDTOIn skillDTOIn) {
        skillService.addSkill(skillDTOIn);
        return ResponseEntity.status(201).body("Skill added successfully");
    }

    @GetMapping
    public ResponseEntity<?> getAllSkills() {
        return ResponseEntity.status(200).body(skillService.getAllSkills());
    }

    @GetMapping("/{skillId}")
    public ResponseEntity<?> getSkillById(@PathVariable Integer skillId) {
        return ResponseEntity.status(200).body(skillService.getSkillById(skillId));
    }

    @PutMapping("/{skillId}")
    public ResponseEntity<?> updateSkill(@PathVariable Integer skillId,
                                         @RequestBody @Valid SkillDTOIn skillDTOIn) {
        skillService.updateSkill(skillId, skillDTOIn);
        return ResponseEntity.status(200).body("Skill updated successfully");
    }

    @DeleteMapping("/{skillId}")
    public ResponseEntity<?> deleteSkill(@PathVariable Integer skillId) {
        skillService.deleteSkill(skillId);
        return ResponseEntity.status(200).body("Skill deleted successfully");
    }
}