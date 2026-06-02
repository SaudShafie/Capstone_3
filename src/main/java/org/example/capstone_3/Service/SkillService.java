package org.example.capstone_3.Service;

import org.example.capstone_3.DTO.IN.SkillDTOIn;
import org.example.capstone_3.DTO.OUT.SkillDTOOut;
import org.example.capstone_3.Model.Skill;
import org.example.capstone_3.Repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    public void addSkill(SkillDTOIn skillDTOIn) {

        Skill skill = new Skill();

        skill.setName(skillDTOIn.getName());
        skill.setCategory(skillDTOIn.getCategory());

        skillRepository.save(skill);
    }

    public List<SkillDTOOut> getAllSkills() {

        List<Skill> skills = skillRepository.findAll();

        List<SkillDTOOut> skillDTOOuts = new ArrayList<>();

        for (Skill skill : skills) {
            skillDTOOuts.add(mapToSkillDTOOut(skill));
        }

        return skillDTOOuts;
    }

    public SkillDTOOut getSkillById(Integer skillId) {

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        return mapToSkillDTOOut(skill);
    }

    public void updateSkill(Integer skillId, SkillDTOIn skillDTOIn) {

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        skill.setName(skillDTOIn.getName());
        skill.setCategory(skillDTOIn.getCategory());

        skillRepository.save(skill);
    }

    public void deleteSkill(Integer skillId) {

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        skillRepository.delete(skill);
    }

    private SkillDTOOut mapToSkillDTOOut(Skill skill) {

        return new SkillDTOOut(
                skill.getId(),
                skill.getName(),
                skill.getCategory()
        );
    }
}