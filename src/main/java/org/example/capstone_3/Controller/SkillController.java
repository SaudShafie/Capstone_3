package org.example.capstone_3.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiResponse;
import org.example.capstone_3.DTO.IN.SkillDTOIn;
import org.example.capstone_3.Service.SkillService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/skill")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @GetMapping("/get")
    public ResponseEntity<?> get() {
        return ResponseEntity.ok(skillService.getAll());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getSkillById(@PathVariable Integer id) {
        return ResponseEntity.ok(skillService.getById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<?> saveSkill(@RequestBody @Valid SkillDTOIn skillDTOIn) {
        skillService.create(skillDTOIn);
        return ResponseEntity.ok().body(new ApiResponse("Skill has been saved successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateSkill(@PathVariable Integer id, @RequestBody @Valid SkillDTOIn skillDTOIn) {
        skillService.update(id, skillDTOIn);
        return ResponseEntity.ok().body(new ApiResponse("Skill has been updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteSkill(@PathVariable Integer id) {
        skillService.delete(id);
        return ResponseEntity.ok().body(new ApiResponse("Skill has been deleted successfully"));
    }
}
