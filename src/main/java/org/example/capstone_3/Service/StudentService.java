package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiException;
import org.example.capstone_3.DTO.IN.StudentDTOIn;
import org.example.capstone_3.DTO.OUT.SkillDTOOut;
import org.example.capstone_3.DTO.OUT.StudentDTOOut;
import org.example.capstone_3.Model.Skill;
import org.example.capstone_3.Model.Student;
import org.example.capstone_3.Repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public void create(StudentDTOIn dto) {
        Student student = new Student();
        applyDto(student, dto);
        student.setXp(0);
        student.setReadinessScore(0);
        student.setCreatedAt(LocalDateTime.now());
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
        return studentRepository.findAll().stream().map(this::toDtoOut).toList();
    }

    public void update(Integer id, StudentDTOIn dto) {
        Student student = studentRepository.findStudentById(id);
        if (student == null) {
            throw new ApiException("Student with id " + id + " not found");
        }
        applyDto(student, dto);
        studentRepository.save(student);
    }

    public void delete(Integer id) {
        Student student = studentRepository.findStudentById(id);
        if (student == null) {
            throw new ApiException("Student with id " + id + " not found");
        }
        studentRepository.deleteById(id);
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
                toSkillDtos(student.getSkills())
        );
    }

    private Set<SkillDTOOut> toSkillDtos(Set<Skill> skills) {
        if (skills == null) {
            return null;
        }
        return skills.stream()
                .map(skill -> new SkillDTOOut(skill.getId(), skill.getName(), skill.getCategory()))
                .collect(Collectors.toSet());
    }
}
