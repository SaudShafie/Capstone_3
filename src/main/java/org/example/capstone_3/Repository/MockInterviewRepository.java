package org.example.capstone_3.Repository;

import org.example.capstone_3.Model.MockInterview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MockInterviewRepository extends JpaRepository<MockInterview, Integer> {

    MockInterview findMockInterviewById(Integer id);

    List<MockInterview> findMockInterviewsByMentorIdAndStatus(Integer mentorId, String status);

    List<MockInterview> findMockInterviewsByStudentId(Integer studentId);

    List<MockInterview> findMockInterviewsByMentorId(Integer mentorId);
}