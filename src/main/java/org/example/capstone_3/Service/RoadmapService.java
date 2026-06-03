package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiException;
import org.example.capstone_3.DTO.IN.RoadmapDTOIN;
import org.example.capstone_3.DTO.OUT.RoadmapDTOOUT;
import org.example.capstone_3.DTO.OUT.RoadmapStepDTOOUT;
import org.example.capstone_3.Model.Roadmap;
import org.example.capstone_3.Model.RoadmapStep;
import org.example.capstone_3.Model.Student;
import org.example.capstone_3.Repository.RoadmapRepository;
import org.example.capstone_3.Repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoadmapService {

    private final RoadmapRepository roadmapRepository;
    private final StudentRepository studentRepository;
    private final RoadmapStepService roadmapStepService;

    public List<RoadmapDTOOUT> getAllRoadmaps() {

        List<RoadmapDTOOUT> roadmapDTO = new ArrayList<>();

        for (Roadmap roadmap : roadmapRepository.findAll()) {
            roadmapDTO.add(convertToDTO(roadmap));
        }
        return roadmapDTO;
    }

    public RoadmapDTOOUT getRoadmapById(Integer id) {
        Roadmap roadmap = roadmapRepository.findRoadmapById(id);

        if (roadmap == null) {
            throw new ApiException("Roadmap not found");
        }

        return convertToDTO(roadmap);
    }

    public void addRoadmap(Integer studentId, RoadmapDTOIN dto) {

        Student student = studentRepository.findStudentById(studentId);

        if (student == null) {
            throw new ApiException("Student not found");
        }

        Roadmap roadmap = new Roadmap();

        roadmap.setTitle(dto.getTitle());
        roadmap.setTargetRole(dto.getTargetRole());
        roadmap.setProgressPercentage(0);
        roadmap.setCreatedAt(LocalDateTime.now());

        roadmap.setStudent(student);

        roadmapRepository.save(roadmap);
    }

    public void updateRoadmap(Integer id, RoadmapDTOIN dto) {

        Roadmap roadmap = roadmapRepository.findRoadmapById(id);

        if (roadmap == null) {
            throw new ApiException("Roadmap not found");
        }

        roadmap.setTitle(dto.getTitle());
        roadmap.setTargetRole(dto.getTargetRole());

        roadmapRepository.save(roadmap);
    }

    public void deleteRoadmap(Integer id) {

        Roadmap roadmap = roadmapRepository.findRoadmapById(id);

        if (roadmap == null) {
            throw new ApiException("Roadmap not found");
        }

        roadmapRepository.delete(roadmap);
    }

    public RoadmapDTOOUT convertToDTO(Roadmap roadmap) {
        List<RoadmapStepDTOOUT> steps = new ArrayList<>();

        if (roadmap.getRoadmapSteps() != null) {
            for (RoadmapStep step : roadmap.getRoadmapSteps()) {
                steps.add(roadmapStepService.convertToDTO(step));
            }
        }
        return new RoadmapDTOOUT(
                roadmap.getId(),
                roadmap.getTitle(),
                roadmap.getTargetRole(),
                roadmap.getProgressPercentage(),
                roadmap.getCreatedAt(),
                roadmap.getStudent() != null ? roadmap.getStudent().getId() : null,
                null,
                steps
        );
    }

}
