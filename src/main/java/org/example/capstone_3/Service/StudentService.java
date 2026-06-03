package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final SkillRepository skillRepository;

    public void create(StudentDTOIn dto) {

        if (studentRepository.findStudentByEmail(dto.getEmail()) != null) {
            throw new ApiException("Email already exists");
        }

        Student student = new Student();

        applyDto(student, dto);

        student.setXp(0);


        student.setReadinessScore(0);
        student.setCreatedAt(LocalDateTime.now());
        student.setSkills(null);

        studentRepository.save(student);
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

        Student student = studentRepository.findStudentById(id);

        if (student == null) {
            throw new ApiException("Student with id " + id + " not found");
        }

        Student emailOwner = studentRepository.findStudentByEmail(dto.getEmail());

        if (emailOwner != null && !emailOwner.getId().equals(id)) {
            throw new ApiException("Email already exists");
        }

        applyDto(student, dto);

        studentRepository.save(student);
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


    // add xp methods
}