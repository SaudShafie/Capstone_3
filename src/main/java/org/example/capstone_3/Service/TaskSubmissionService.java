package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiException;
import org.example.capstone_3.DTO.IN.TaskSubmissionDTOIN;
import org.example.capstone_3.DTO.OUT.TaskSubmissionDTOOUT;
import org.example.capstone_3.Model.Student;
import org.example.capstone_3.Model.Task;
import org.example.capstone_3.Model.TaskSubmission;
import org.example.capstone_3.Repository.StudentRepository;
import org.example.capstone_3.Repository.TaskRepository;
import org.example.capstone_3.Repository.TaskSubmissionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskSubmissionService {
    private final TaskSubmissionRepository taskSubmissionRepository;
    private final TaskRepository taskRepository;
    private final StudentRepository studentRepository;

    public List<TaskSubmissionDTOOUT> getAllTaskSubmissions() {

        List<TaskSubmissionDTOOUT> submissionDTO = new ArrayList<>();

        for (TaskSubmission submission : taskSubmissionRepository.findAll()) {
            submissionDTO.add(convertToDTO(submission));
        }

        return submissionDTO;
    }

    public TaskSubmissionDTOOUT getTaskSubmissionById(Integer id) {

        TaskSubmission submission = taskSubmissionRepository.findTaskSubmissionById(id);

        if (submission == null) {
            throw new ApiException("Task Submission not found");
        }

        return convertToDTO(submission);
    }

    public void addTaskSubmission(Integer taskId, Integer studentId, TaskSubmissionDTOIN dto) {

        Task task = taskRepository.findTaskById(taskId);

        if (task == null) {
            throw new ApiException("Task not found");
        }

        Student student = studentRepository.findStudentById(studentId);

        if (student == null) {
            throw new ApiException("Student not found");
        }

        TaskSubmission submission = new TaskSubmission();

        submission.setAnswerText(dto.getAnswerText());
        submission.setCorrect(false);
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setAiFeedback(null);

        submission.setTask(task);
        submission.setStudent(student);

        taskSubmissionRepository.save(submission);
    }

    public void updateTaskSubmission(Integer id, TaskSubmissionDTOIN dto) {

        TaskSubmission submission = taskSubmissionRepository.findTaskSubmissionById(id);

        if (submission == null) {
            throw new ApiException("Task Submission not found");
        }

        submission.setAnswerText(dto.getAnswerText());

        taskSubmissionRepository.save(submission);
    }

    public void deleteTaskSubmission(Integer id) {

        TaskSubmission submission = taskSubmissionRepository.findTaskSubmissionById(id);

        if (submission == null) {
            throw new ApiException("Task Submission not found");
        }

        taskSubmissionRepository.delete(submission);
    }

    public TaskSubmissionDTOOUT convertToDTO(TaskSubmission submission) {

        String taskTitle = null;

        if (submission.getTask() != null) {
            taskTitle = submission.getTask().getTitle();
        }

        return new TaskSubmissionDTOOUT(
                submission.getId(),
                taskTitle,
                submission.getAnswerText(),
                submission.getAiFeedback(),
                submission.getCorrect(),
                submission.getSubmittedAt()
        );
    }
}
