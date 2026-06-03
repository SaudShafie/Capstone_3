package org.example.capstone_3.Repository;

import org.example.capstone_3.Model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

    Student findStudentById(Integer id);

    Student findStudentByEmail(String email);

    List<Student> findStudentsByTargetRoleContainingIgnoreCase(String targetRole);

    List<Student> findStudentsByMajorContainingIgnoreCase(String major);
}