package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiException;
import org.example.capstone_3.DTO.IN.LearningGroupDTOIN;
import org.example.capstone_3.DTO.OUT.LearningGroupDTOOUT;
import org.example.capstone_3.DTO.OUT.TaskDTOOUT;
import org.example.capstone_3.Model.LearningGroup;
import org.example.capstone_3.Model.Student;
import org.example.capstone_3.Model.Task;
import org.example.capstone_3.Repository.LearningGroupRepository;
import org.example.capstone_3.Repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LearningGroupService {

    private final LearningGroupRepository learningGroupRepository;
    private final TaskService taskService;
    private final StudentRepository studentRepository;

    public List<LearningGroupDTOOUT> getAllLearningGroups() {

        List<LearningGroupDTOOUT> groupDTO = new ArrayList<>();

        for (LearningGroup learningGroup : learningGroupRepository.findAll()) {
            groupDTO.add(convertToDTO(learningGroup));
        }

        return groupDTO;
    }

    public LearningGroupDTOOUT getLearningGroupById(Integer id) {

        LearningGroup learningGroup = learningGroupRepository.findLearningGroupById(id);

        if (learningGroup == null) {
            throw new ApiException("Learning Group not found");
        }

        return convertToDTO(learningGroup);
    }

    public void addLearningGroup(Integer student_id, LearningGroupDTOIN dto) {

        Student student = studentRepository.findStudentById(student_id);
        if(student == null){
            throw new ApiException("Student not exist");
        }

        LearningGroup learningGroup = new LearningGroup();

        learningGroup.setName(dto.getName());
        learningGroup.setFocusArea(dto.getFocusArea());
        learningGroup.setDescription(dto.getDescription());
        learningGroup.setCreatedAt(LocalDateTime.now());

        learningGroup.getStudents().add(student);
        student.getLearningGroups().add(learningGroup);

        learningGroupRepository.save(learningGroup);
        studentRepository.save(student);
    }

    public void updateLearningGroup(Integer id, LearningGroupDTOIN dto) {

        LearningGroup learningGroup = learningGroupRepository.findLearningGroupById(id);

        if (learningGroup == null) {
            throw new ApiException("Learning Group not found");
        }

        learningGroup.setName(dto.getName());
        learningGroup.setFocusArea(dto.getFocusArea());
        learningGroup.setDescription(dto.getDescription());

        learningGroupRepository.save(learningGroup);
    }

    public void deleteLearningGroup(Integer id) {

        LearningGroup learningGroup = learningGroupRepository.findLearningGroupById(id);

        if (learningGroup == null) {
            throw new ApiException("Learning Group not found");
        }

        learningGroupRepository.delete(learningGroup);
    }

    public LearningGroupDTOOUT convertToDTO(LearningGroup learningGroup) {

        List<TaskDTOOUT> tasks = new ArrayList<>();

        if (learningGroup.getTasks() != null) {
            for (Task task : learningGroup.getTasks()) {
                tasks.add(taskService.convertToDTO(task));
            }
        }

        return new LearningGroupDTOOUT(
                learningGroup.getId(),
                learningGroup.getName(),
                learningGroup.getFocusArea(),
                learningGroup.getDescription(),
                tasks,
                learningGroup.getCreatedAt()
        );
    }
}
