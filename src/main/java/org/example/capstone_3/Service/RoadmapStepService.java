package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiException;
import org.example.capstone_3.DTO.IN.RoadmapStepDTOIN;
import org.example.capstone_3.DTO.OUT.RoadmapStepDTOOUT;
import org.example.capstone_3.Model.Roadmap;
import org.example.capstone_3.Model.RoadmapStep;
import org.example.capstone_3.Model.Skill;
import org.example.capstone_3.Repository.RoadmapRepository;
import org.example.capstone_3.Repository.RoadmapStepRepository;
import org.example.capstone_3.Repository.SkillRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoadmapStepService {
    private final RoadmapStepRepository roadmapStepRepository;
    private final RoadmapRepository roadmapRepository;
    private final SkillRepository skillRepository;

    public List<RoadmapStepDTOOUT> getAllRoadmapSteps() {
        List<RoadmapStepDTOOUT> stepDTO = new ArrayList<>();

        for (RoadmapStep roadmapStep : roadmapStepRepository.findAll()) {
            stepDTO.add(convertToDTO(roadmapStep));
        }
        return stepDTO;
    }

    public RoadmapStepDTOOUT getRoadmapStepById(Integer id){
        RoadmapStep roadmapStep = roadmapStepRepository.findRoadmapStepById(id);
        if(roadmapStep == null){
            throw new ApiException("Roadmap Step not found");
        }
        return convertToDTO(roadmapStep);
    }

    public void addRoadmapStep(Integer roadmap_id, RoadmapStepDTOIN dto) {
        Roadmap roadmap = roadmapRepository.findRoadmapById(roadmap_id);
        if(roadmap == null){
            throw new ApiException("Roadmap not found");
        }

        Skill skill = skillRepository.findSkillById(dto.getSkillId());
        if(skill == null){
            throw new ApiException("Skill not exist");
        }
        RoadmapStep roadmapStep = new RoadmapStep();

        roadmapStep.setTitle(dto.getTitle());
        roadmapStep.setDescription(dto.getDescription());
        roadmapStep.setOrderNumber(dto.getOrderNumber());
        roadmapStep.setCompleted(false);
        roadmapStep.setCompletedAt(LocalDateTime.now());

        roadmapStep.setRoadmap(roadmap);
        roadmapStep.setSkill(skill);
        roadmapStepRepository.save(roadmapStep);
    }

    public void updateRoadmapStep(Integer id, RoadmapStepDTOIN dto){
        RoadmapStep roadmapStep = roadmapStepRepository.findRoadmapStepById(id);
        if(roadmapStep == null){
            throw new ApiException("Roadmap Step not found");
        }
        Skill skill = skillRepository.findSkillById(dto.getSkillId());
        if(skill == null){
            throw new ApiException("Skill not exist");
        }
        roadmapStep.setTitle(dto.getTitle());
        roadmapStep.setDescription(dto.getDescription());
        roadmapStep.setOrderNumber(dto.getOrderNumber());
        roadmapStep.setSkill(skill);

        roadmapStepRepository.save(roadmapStep);
    }

    public void deleteRoadmapStep(Integer id){
        RoadmapStep roadmapStep = roadmapStepRepository.findRoadmapStepById(id);
        if(roadmapStep == null){
            throw new ApiException("Roadmap Step not found");
        }
        roadmapStepRepository.delete(roadmapStep);
    }

    public RoadmapStepDTOOUT convertToDTO(RoadmapStep roadmapStep) {

        String skillName = null;

        if (roadmapStep.getSkill() != null) {
            skillName = roadmapStep.getSkill().getName();
        }

        return new RoadmapStepDTOOUT(
                roadmapStep.getId(),
                roadmapStep.getTitle(),
                roadmapStep.getDescription(),
                roadmapStep.getOrderNumber(),
                roadmapStep.getCompleted(),
                null,
                skillName,
                roadmapStep.getCompletedAt()
        );
    }
}
