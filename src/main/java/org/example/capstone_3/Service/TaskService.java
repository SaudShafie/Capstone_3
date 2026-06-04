package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.AI.AiException;
import org.example.capstone_3.AI.AiService;
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
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TaskService {
    // Add these patterns at the top of the class
    private static final Pattern TITLE_PATTERN =
            Pattern.compile("\"title\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern DESCRIPTION_PATTERN =
            Pattern.compile("\"description\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern POINTS_PATTERN =
            Pattern.compile("\"points\"\\s*:\\s*(\\d+)");
    private static final Pattern DIFFICULTY_PATTERN =
            Pattern.compile("\"difficulty\"\\s*:\\s*\"(EASY|MEDIUM|HARD)\"");
    private static final Pattern DEADLINE_DAYS_PATTERN =
            Pattern.compile("\"deadlineDays\"\\s*:\\s*(\\d+)");

    private final TaskRepository taskRepository;
    private final LearningGroupRepository learningGroupRepository;
    private final AiService aiService;

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

    public void addTask(Integer learningGroupId) {
        LearningGroup learningGroup = learningGroupRepository.findLearningGroupById(learningGroupId);
        if (learningGroup == null) {
            throw new ApiException("Learning group not found");
        }

        Task task = fetchTaskFromAi(learningGroup);
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
                task.getDifficulty(),
                task.getDeadline(),
                task.getCreatedAt()
        );
    }

    // AI

    private Task fetchTaskFromAi(LearningGroup learningGroup) {
        List<Task> existingTasks = taskRepository.findTasksByLearningGroupId(learningGroup.getId());
        String prompt = buildTaskPrompt(learningGroup, existingTasks);
        String json = aiService.ask(prompt);
        Task task = parseTaskJson(json);
        task.setPoints(mapPoints(task.getDifficulty()));
        return task;
    }

    private String buildTaskPrompt(LearningGroup learningGroup, List<Task> existingTasks) {
        String difficulty = randomDifficulty();

        StringBuilder existingTasksList = new StringBuilder();
        if (!existingTasks.isEmpty()) {
            existingTasksList.append("ALREADY GENERATED TASKS — DO NOT REUSE OR PARAPHRASE:\n");
            for (int i = 0; i < existingTasks.size(); i++) {
                existingTasksList.append(i + 1)
                        .append(". ").append(existingTasks.get(i).getTitle())
                        .append(": ").append(existingTasks.get(i).getDescription())
                        .append("\n");
            }
        }

        return """
            You are a strict professional task generator for a collaborative learning platform.
            Your ONLY job is to generate a single realistic, time-appropriate task for the learning group below.

            LEARNING GROUP CONTEXT:
            - Focus Area: "%s"
            - Description: "%s"

            CRITICAL RULES:
            - The task must be 100%% relevant to the focus area "%s"
            - The task must align with the group description and purpose
            - Do NOT generate generic or tutorial-style tasks
            - The task must be a real-world, practical assignment
            - The task scope and complexity MUST be realistically achievable within the deadline
            - NEVER repeat or paraphrase previous tasks

            %s

            UNIQUENESS RULES:
            - Generate a completely new task
            - Do NOT reuse any previous scenario or problem type
            - Similar meaning counts as duplication even if wording is different
            - Prefer unexplored aspects of the focus area

            Today's date is: %s

            Respond with JSON only:
            {
              "title": "...",
              "description": "...",
              "difficulty": "%s",
              "deadlineDays": <integer>
            }

            FIELD RULES:

            - title:
              * max 10 words
              * must reflect the focus area "%s"
              * must give a clear idea of what will be done

            - description:
              * max 80 words
              * must be a concrete, actionable assignment
              * must be realistic to complete within the deadline
              * must clearly state what needs to produce or deliver
              * must reflect the difficulty level and time available
              * avoid vague instructions like "research and discuss" — be specific about the deliverable

            - difficulty: must be exactly: %s
              * EASY:
                - Simple, well-defined task
                - Requires basic knowledge of the focus area
                - Minimal coordination needed
                - Example scope: read, summarize, or implement one small thing
              * MEDIUM:
                - Requires applying knowledge to a realistic problem
                - Involves collaboration and decision-making
                - Example scope: design, build, or analyze something with multiple steps
              * HARD:
                - Complex, open-ended challenge
                - Requires deep expertise and strong teamwork
                - Example scope: architect, evaluate, or solve a non-trivial real-world problem

            - deadlineDays:
              * integer only (1–7)
              * MUST match the task scope — the task must be fully completable within this time
              * EASY → 1–2 days (small, focused deliverable)
              * MEDIUM → 3–4 days (multi-step work requiring collaboration)
              * HARD → 5–7 days (complex work requiring significant effort)
              * Do NOT assign a 1-day deadline to a task that requires significant research or implementation
              * Do NOT assign a 7-day deadline to a simple task

            REALISM VALIDATION:
            - Ask yourself: can a group realistically complete this task in the given deadlineDays?
            - If NO → reduce scope or increase deadlineDays
            - The description must clearly reflect a deliverable that fits within the deadline
            - A 1-day task should have a narrow, specific deliverable
            - A 7-day task should have a broad, complex deliverable

            OUTPUT MUST BE STRICT JSON ONLY
            """.formatted(
                learningGroup.getFocusArea(),
                learningGroup.getDescription(),
                learningGroup.getFocusArea(),
                existingTasksList.toString(),
                LocalDateTime.now(),
                difficulty,
                learningGroup.getFocusArea(),
                difficulty
        );
    }
    private Task parseTaskJson(String json) {
        Matcher titleMatcher = TITLE_PATTERN.matcher(json);
        Matcher descriptionMatcher = DESCRIPTION_PATTERN.matcher(json);
        Matcher difficultyMatcher = DIFFICULTY_PATTERN.matcher(json);
        Matcher deadlineDaysMatcher = DEADLINE_DAYS_PATTERN.matcher(json);

        if (!titleMatcher.find()) throw new AiException("AI response did not contain title.");
        if (!descriptionMatcher.find()) throw new AiException("AI response did not contain description.");
        if (!difficultyMatcher.find()) throw new AiException("AI response did not contain difficulty.");
        if (!deadlineDaysMatcher.find()) throw new AiException("AI response did not contain deadlineDays.");

        int deadlineDays = Integer.parseInt(deadlineDaysMatcher.group(1));
        if (deadlineDays < 2 || deadlineDays > 14) {
            throw new AiException("AI generated invalid deadlineDays (must be 2–14).");
        }

        Task task = new Task();
        task.setTitle(titleMatcher.group(1));
        task.setDescription(descriptionMatcher.group(1));
        task.setDifficulty(difficultyMatcher.group(1));
        task.setDeadline(LocalDateTime.now().plusDays(deadlineDays));
        return task;
    }

    private int mapPoints(String difficulty) {
        return switch (difficulty) {
            case "EASY" -> 10;
            case "MEDIUM" -> 20;
            case "HARD" -> 30;
            default -> throw new AiException("Invalid difficulty: " + difficulty);
        };
    }

    private String randomDifficulty() {
        String[] levels = {"EASY", "MEDIUM", "HARD"};
        return levels[new Random().nextInt(levels.length)];
    }
}
