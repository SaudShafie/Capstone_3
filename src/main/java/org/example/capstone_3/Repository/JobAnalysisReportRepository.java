package org.example.capstone_3.Repository;

import org.example.capstone_3.Model.JobAnalysisReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobAnalysisReportRepository extends JpaRepository<JobAnalysisReport, Integer> {

    JobAnalysisReport findJobAnalysisReportById(Integer id);
}
