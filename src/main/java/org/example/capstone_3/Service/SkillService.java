package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiException;
import org.example.capstone_3.DTO.IN.SkillDTOIn;
import org.example.capstone_3.DTO.OUT.SkillDTOOut;
import org.example.capstone_3.Model.Skill;
import org.example.capstone_3.Repository.SkillRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillDTOOut create(SkillDTOIn dto) {
        Skill skill = new Skill();
        applyDto(skill, dto);
        return toDtoOut(skillRepository.save(skill));
    }

    public SkillDTOOut getById(Integer id) {
        Skill skill = skillRepository.findSkillById(id);
        if (skill == null) {
            throw new ApiException("Skill with id " + id + " not found");
        }
        return toDtoOut(skill);
    }

    public List<SkillDTOOut> getAll() {
        return skillRepository.findAll().stream().map(this::toDtoOut).toList();
    }

    public SkillDTOOut update(Integer id, SkillDTOIn dto) {
        Skill skill = skillRepository.findSkillById(id);
        if (skill == null) {
            throw new ApiException("Skill with id " + id + " not found");
        }
        applyDto(skill, dto);
        return toDtoOut(skillRepository.save(skill));
    }

    public void delete(Integer id) {
        Skill skill = skillRepository.findSkillById(id);
        if (skill == null) {
            throw new ApiException("Skill with id " + id + " not found");
        }
        skillRepository.deleteById(id);
    }

    private void applyDto(Skill skill, SkillDTOIn dto) {
        skill.setName(dto.getName());
        skill.setCategory(dto.getCategory());
    }

    private SkillDTOOut toDtoOut(Skill skill) {
        return new SkillDTOOut(
                skill.getId(),
                skill.getName(),
                skill.getCategory()
        );
    }
}
