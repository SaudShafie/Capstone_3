package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.AI.AiException;
import org.example.capstone_3.AI.AiService;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TaskSubmissionService {
    private static final Pattern SCORE_PATTERN =
            Pattern.compile("\"score\"\\s*:\\s*(\\d+)");
    private static final Pattern FEEDBACK_PATTERN =
            Pattern.compile("\"feedback\"\\s*:\\s*\"([^\"]+)\"");

    private final TaskSubmissionRepository taskSubmissionRepository;
    private final TaskRepository taskRepository;
    private final StudentRepository studentRepository;
    private final AiService aiService;


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
        Task task = findTask(taskId);
        Student student = findStudent(studentId);

        boolean alreadyEarned = taskSubmissionRepository.findPassingSubmission(taskId, studentId, 75) != null;

        String[] result = fetchEvaluationFromAi(dto.getAnswerText(), task);
        int score = Integer.parseInt(result[0]);

        TaskSubmission submission = new TaskSubmission();
        submission.setAnswerText(dto.getAnswerText());
        submission.setScore(score);
        submission.setAiFeedback(result[1]);
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setTask(task);
        submission.setStudent(student);

        if (!alreadyEarned && score >= 75 && LocalDateTime.now().isBefore(task.getDeadline())) {
            student.setXp(student.getXp() + task.getPoints());
            studentRepository.save(student);
        }

        taskSubmissionRepository.save(submission);
    }

    public void updateTaskSubmission(Integer id, TaskSubmissionDTOIN dto) {
        TaskSubmission submission = findTaskSubmission(id);

        Task task = submission.getTask();
        Student student = submission.getStudent();

        boolean alreadyEarned = taskSubmissionRepository
                .findPassingSubmission(task.getId(), student.getId(), 75) != null;

        String[] result = fetchEvaluationFromAi(dto.getAnswerText(), task);
        int newScore = Integer.parseInt(result[0]);

        submission.setAnswerText(dto.getAnswerText());
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setScore(newScore);
        submission.setAiFeedback(result[1]);

        if (!alreadyEarned && newScore >= 75 && LocalDateTime.now().isBefore(task.getDeadline())) {
            student.setXp(student.getXp() + task.getPoints());
        }

        studentRepository.save(student);
        taskSubmissionRepository.save(submission);
    }

    public void deleteTaskSubmission(Integer id) {
        TaskSubmission submission = findTaskSubmission(id);
        taskSubmissionRepository.delete(submission);
    }

    public List<TaskSubmissionDTOOUT> studentTaskSubmissions(Integer studentId, Integer taskId) {
        findStudent(studentId);
        findTask(taskId);

        List<TaskSubmissionDTOOUT> submissions = new ArrayList<>();
        for (TaskSubmission submission : taskSubmissionRepository.findByTaskIdAndStudentId(taskId, studentId)) {
            submissions.add(convertToDTO(submission));
        }
        return submissions;
    }

    private Task findTask(Integer taskId) {
        Task task = taskRepository.findTaskById(taskId);
        if (task == null) {
            throw new ApiException("Task not found");
        }
        return task;
    }

    private Student findStudent(Integer studentId) {
        Student student = studentRepository.findStudentById(studentId);
        if (student == null) {
            throw new ApiException("Student not found");
        }
        return student;
    }

    private TaskSubmission findTaskSubmission(Integer id) {
        TaskSubmission submission = taskSubmissionRepository.findTaskSubmissionById(id);
        if (submission == null) {
            throw new ApiException("Task Submission not found");
        }
        return submission;
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
                submission.getScore(),
                submission.getSubmittedAt()
        );
    }

    //AI

    private String[] fetchEvaluationFromAi(String answerText, Task task) {
        String prompt = buildEvaluationPrompt(answerText, task);
        String json = aiService.ask(prompt);
        return parseEvaluationJson(json);
    }

    private String buildEvaluationPrompt(String answerText, Task task) {
        return """
        You are a strict professional task evaluator for a collaborative learning platform.
        Your ONLY job is to evaluate a student's submitted answer for the task below.

        TASK CONTEXT:
        - Title: "%s"
        - Description: "%s"
        - Difficulty: "%s"

        EVALUATION RULES:
        - Evaluate how well the student's answer addresses the task description
        - Score must reflect the quality, completeness, and relevance of the answer
        - Do NOT give full marks for incomplete answers
        - Provide constructive, specific feedback explaining the score
        - Feedback must mention what was done well and what was missing
        - Do NOT deduct points for missing comments, code style, or documentation
        - Only deduct for missing functionality, wrong logic, or incomplete implementation
        - Suggestions about comments or style should appear in feedback only, not affect the score

        STUDENT ANSWER:
        %s

        Respond with JSON only:
        {
          "score": <integer>,
          "feedback": "..."
        }

        FIELD RULES:
        - score:
          * integer between 0 and 100
          * EASY task: full marks for correct basic answer, deduct for missing key points
          * MEDIUM task: full marks only for complete and well-reasoned answer
          * HARD task: full marks for a solid, well-explained answer — do NOT require perfection,
            reward good understanding and effort even if not expert-level
          * For HARD tasks, a good-faith attempt with correct core concepts should score at least 60
          * 0 = completely wrong or irrelevant
          * 100 = perfect, complete, professional answer

        - feedback:
          * max 80 words
          * must be specific to the student's answer
          * must explain why the score was given
          * must mention what was good and what needs improvement
          * must be constructive and professional

        OUTPUT MUST BE STRICT JSON ONLY
        """.formatted(
                task.getTitle(),
                task.getDescription(),
                task.getDifficulty(),
                answerText
        );
    }

    private String[] parseEvaluationJson(String json) {
        Matcher scoreMatcher = SCORE_PATTERN.matcher(json);
        Matcher feedbackMatcher = FEEDBACK_PATTERN.matcher(json);

        if (!scoreMatcher.find()) throw new AiException("AI response did not contain score.");
        if (!feedbackMatcher.find()) throw new AiException("AI response did not contain feedback.");

        int score = Integer.parseInt(scoreMatcher.group(1));
        if (score < 0 || score > 100) {
            throw new AiException("AI generated invalid score (must be 0–100).");
        }

        return new String[]{String.valueOf(score), feedbackMatcher.group(1)};
    }
}
