package org.example.capstone_3.Service;

import org.example.capstone_3.DTO.IN.StudentDTOIn;
import org.example.capstone_3.DTO.OUT.StudentDTOOut;
import org.example.capstone_3.Model.Student;
import org.example.capstone_3.Repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public void addStudent(StudentDTOIn studentDTOIn) {

        Student student = new Student();

        student.setFullName(studentDTOIn.getFullName());
        student.setEmail(studentDTOIn.getEmail());
        student.setPassword(studentDTOIn.getPassword());
        student.setMajor(studentDTOIn.getMajor());
        student.setTargetRole(studentDTOIn.getTargetRole());
        student.setYearsExperience(studentDTOIn.getYearsExperience());
        student.setLinkedinUrl(studentDTOIn.getLinkedinUrl());
        student.setGithubUrl(studentDTOIn.getGithubUrl());
        student.setCvText(studentDTOIn.getCvText());

        student.setXp(0);
        student.setReadinessScore(0);
        student.setCreatedAt(LocalDateTime.now());

        studentRepository.save(student);
    }

    public List<StudentDTOOut> getAllStudents() {

        List<Student> students = studentRepository.findAll();

        List<StudentDTOOut> studentDTOOuts = new ArrayList<>();

        for (Student student : students) {
            studentDTOOuts.add(mapToStudentDTOOut(student));
        }

        return studentDTOOuts;
    }

    public StudentDTOOut getStudentById(Integer studentId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return mapToStudentDTOOut(student);
    }

    public void updateStudent(Integer studentId, StudentDTOIn studentDTOIn) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        student.setFullName(studentDTOIn.getFullName());
        student.setEmail(studentDTOIn.getEmail());
        student.setPassword(studentDTOIn.getPassword());
        student.setMajor(studentDTOIn.getMajor());
        student.setTargetRole(studentDTOIn.getTargetRole());
        student.setYearsExperience(studentDTOIn.getYearsExperience());
        student.setLinkedinUrl(studentDTOIn.getLinkedinUrl());
        student.setGithubUrl(studentDTOIn.getGithubUrl());
        student.setCvText(studentDTOIn.getCvText());

        studentRepository.save(student);
    }

    public void deleteStudent(Integer studentId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        studentRepository.delete(student);
    }

    private StudentDTOOut mapToStudentDTOOut(Student student) {

        return new StudentDTOOut(
                student.getId(),
                student.getFullName(),
                student.getEmail(),
                student.getMajor(),
                student.getTargetRole(),
                student.getYearsExperience(),
                student.getLinkedinUrl(),
                student.getGithubUrl(),
                student.getXp()
        );
    }
}