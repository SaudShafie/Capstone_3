package org.example.capstone_3.Repository;

import org.example.capstone_3.Model.MockInterviewReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MockInterviewReportRepository extends JpaRepository<MockInterviewReport, Integer> {

    MockInterviewReport findMockInterviewReportById(Integer id);
}
