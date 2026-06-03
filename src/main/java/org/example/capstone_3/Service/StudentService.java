package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.AI.AiException;
import org.example.capstone_3.AI.AiService;
import org.example.capstone_3.Api.ApiException;
import org.example.capstone_3.DTO.IN.StudentDTOIn;
import org.example.capstone_3.DTO.OUT.SkillDTOOut;
import org.example.capstone_3.DTO.OUT.StudentDTOOut;
import org.example.capstone_3.Model.Skill;
import org.example.capstone_3.Model.Student;
import org.example.capstone_3.Repository.SkillRepository;
import org.example.capstone_3.Repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class StudentService {

    private static final Pattern READINESS_SCORE_PATTERN =
            Pattern.compile("\"readinessScore\"\\s*:\\s*(\\d+)");

    private final StudentRepository studentRepository;
    private final SkillRepository skillRepository;
    private final AiService aiService;

    /**
     * Add student: validate → AI readiness score → save.
     * Request body validation is done in the controller ({@code @Valid}).
     */
    public void addStudent(StudentDTOIn dto) {
        if (studentRepository.findStudentByEmail(dto.getEmail()) != null) {
            throw new ApiException("Email already exists");
        }

        Student student = new Student();
        applyDto(student, dto);
        student.setXp(0);
        student.setCreatedAt(LocalDateTime.now());
        student.setSkills(null);
        student.setReadinessScore(fetchReadinessScoreFromAi(dto));

        studentRepository.save(student);
    }

    /**
     * Update student: validate → AI readiness score → save.
     */
    public void updateStudent(Integer id, StudentDTOIn dto) {
        Student student = studentRepository.findStudentById(id);

        if (student == null) {
            throw new ApiException("Student with id " + id + " not found");
        }

        Student emailOwner = studentRepository.findStudentByEmail(dto.getEmail());

        if (emailOwner != null && !emailOwner.getId().equals(id)) {
            throw new ApiException("Email already exists");
        }

        applyDto(student, dto);
        student.setReadinessScore(fetchReadinessScoreFromAi(dto));

        studentRepository.save(student);
    }

    public void create(StudentDTOIn dto) {
        addStudent(dto);
    }

    public StudentDTOOut getById(Integer id) {

        Student student = studentRepository.findStudentById(id);

        if (student == null) {
            throw new ApiException("Student with id " + id + " not found");
        }

        return toDtoOut(student);
    }

    public List<StudentDTOOut> getAll() {

        List<Student> students = studentRepository.findAll();

        List<StudentDTOOut> studentDTOOuts = new ArrayList<>();

        for (Student student : students) {
            studentDTOOuts.add(toDtoOut(student));
        }

        return studentDTOOuts;
    }

    public void update(Integer id, StudentDTOIn dto) {
        updateStudent(id, dto);
    }

    public void delete(Integer id) {

        Student student = studentRepository.findStudentById(id);

        if (student == null) {
            throw new ApiException("Student with id " + id + " not found");
        }

        studentRepository.delete(student);
    }

    public void addSkillToStudent(Integer studentId, Integer skillId) {

        Student student = studentRepository.findStudentById(studentId);

        if (student == null) {
            throw new ApiException("Student with id " + studentId + " not found");
        }

        Skill skill = skillRepository.findSkillById(skillId);

        if (skill == null) {
            throw new ApiException("Skill with id " + skillId + " not found");
        }

        if (student.getSkills() == null) {
            student.setSkills(new HashSet<>());
        }

        for (Skill existingSkill : student.getSkills()) {
            if (existingSkill.getId().equals(skillId)) {
                throw new ApiException("Student already has this skill");
            }
        }

        student.getSkills().add(skill);
        studentRepository.save(student);
    }

    public void removeSkillFromStudent(Integer studentId, Integer skillId) {

        Student student = studentRepository.findStudentById(studentId);

        if (student == null) {
            throw new ApiException("Student with id " + studentId + " not found");
        }

        Skill skill = skillRepository.findSkillById(skillId);

        if (skill == null) {
            throw new ApiException("Skill with id " + skillId + " not found");
        }

        if (student.getSkills() == null || student.getSkills().isEmpty()) {
            throw new ApiException("Student does not have any skills");
        }

        boolean removed = student.getSkills().removeIf(existingSkill ->
                existingSkill.getId().equals(skillId)
        );

        if (!removed) {
            throw new ApiException("Student does not have this skill");
        }

        studentRepository.save(student);
    }
/**
 * هنا الطلب من ال AI service
 */

    private int fetchReadinessScoreFromAi(StudentDTOIn dto) {
        // البرومت طلبناه من البلدير
        String prompt = buildReadinessPrompt(dto);
        String json = aiService.ask(prompt);
        return parseReadinessScore(json);
    }
    /**
     * ذا الي يسوي ال prompt
     */
    private String buildReadinessPrompt(StudentDTOIn dto) {
        String cv = dto.getCvText() == null || dto.getCvText().isBlank() ? "(no CV provided)" : dto.getCvText();
        return """
                You are a career advisor for university students.
                Estimate job readiness for the target role based on the profile below.
                
                Respond with JSON only using this exact shape:
                {"readinessScore": 0}
                
                readinessScore must be an integer from 0 to 100.
                
                Major: %s
                Target role: %s
                Years of experience: %s
                
                --- CV ---
                %s
                """.formatted(
                dto.getMajor(),
                dto.getTargetRole(),
                dto.getYearsExperience(),
                cv
        );
    }
    /**
     * ذا بياخذ الاجابه ويربطها 
     */
    private int parseReadinessScore(String json) {
        Matcher matcher = READINESS_SCORE_PATTERN.matcher(json);
        if (!matcher.find()) {
            throw new AiException("AI response did not contain readinessScore.");
        }
        int score = Integer.parseInt(matcher.group(1));
        if (score < 0 || score > 100) {
            throw new AiException("AI readinessScore must be between 0 and 100.");
        }
        return score;
    }

    private void applyDto(Student student, StudentDTOIn dto) {
        student.setFullName(dto.getFullName());
        student.setEmail(dto.getEmail());
        student.setPassword(dto.getPassword());
        student.setMajor(dto.getMajor());
        student.setTargetRole(dto.getTargetRole());
        student.setYearsExperience(dto.getYearsExperience());
        student.setLinkedinUrl(dto.getLinkedinUrl());
        student.setGithubUrl(dto.getGithubUrl());
        student.setCvText(dto.getCvText());
    }

    private StudentDTOOut toDtoOut(Student student) {
        return new StudentDTOOut(
                student.getId(),
                student.getFullName(),
                student.getEmail(),
                student.getMajor(),
                student.getTargetRole(),
                student.getYearsExperience(),
                student.getLinkedinUrl(),
                student.getGithubUrl(),
                student.getCvText(),
                student.getXp(),
                student.getReadinessScore(),
                mapSkills(student.getSkills())
        );
    }

    private Set<SkillDTOOut> mapSkills(Set<Skill> skills) {

        Set<SkillDTOOut> skillDTOOuts = new HashSet<>();

        if (skills == null) {
            return skillDTOOuts;
        }

        for (Skill skill : skills) {
            SkillDTOOut skillDTOOut = new SkillDTOOut(
                    skill.getId(),
                    skill.getName(),
                    skill.getCategory()
            );

            skillDTOOuts.add(skillDTOOut);
        }

        return skillDTOOuts;
    }
}
