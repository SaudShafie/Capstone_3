package org.example.capstone_3.Repository;

import org.example.capstone_3.Model.MockInterview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MockInterviewRepository extends JpaRepository<MockInterview, Integer> {

    MockInterview findMockInterviewById(Integer id);
}
