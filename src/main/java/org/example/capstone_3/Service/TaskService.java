package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiException;
import org.example.capstone_3.DTO.IN.TaskDTOIN;
import org.example.capstone_3.DTO.OUT.TaskDTOOUT;
import org.example.capstone_3.Model.LearningGroup;
import org.example.capstone_3.Model.Task;
import org.example.capstone_3.Repository.LearningGroupRepository;
import org.example.capstone_3.Repository.TaskRepository;
import org.example.capstone_3.Repository.TaskSubmissionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final LearningGroupRepository learningGroupRepository;
    private final TaskSubmissionRepository taskSubmissionRepository;

    public List<TaskDTOOUT> getAllTasks() {

        List<TaskDTOOUT> taskDTO = new ArrayList<>();

        for (Task task : taskRepository.findAll()) {
            taskDTO.add(convertToDTO(task));
        }

        return taskDTO;
    }

    public TaskDTOOUT getTaskById(Integer id) {

        Task task = taskRepository.findTaskById(id);

        if (task == null) {
            throw new ApiException("Task not found");
        }

        return convertToDTO(task);
    }

    public void addTask(Integer learningGroupId, TaskDTOIN dto) {

        LearningGroup learningGroup = learningGroupRepository.findLearningGroupById(learningGroupId);

        if (learningGroup == null) {
            throw new ApiException("Learning group not found");
        }

        Task task = new Task();

        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setDeadline(dto.getDeadline());
        task.setOpen(true);
        task.setCreatedAt(LocalDateTime.now());

        task.setLearningGroup(learningGroup);

        taskRepository.save(task);
    }

    public void updateTask(Integer id, TaskDTOIN dto) {

        Task task = taskRepository.findTaskById(id);

        if (task == null) {
            throw new ApiException("Task not found");
        }

        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setDeadline(dto.getDeadline());

        taskRepository.save(task);
    }

    public void deleteTask(Integer id) {

        Task task = taskRepository.findTaskById(id);

        if (task == null) {
            throw new ApiException("Task not found");
        }

        taskRepository.delete(task);
    }

    public TaskDTOOUT convertToDTO(Task task) {

        return new TaskDTOOUT(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDeadline(),
                task.getOpen(),
                task.getCreatedAt()
        );
    }
}
