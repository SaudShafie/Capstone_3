package org.example.capstone_3.Repository;

import org.example.capstone_3.Model.JobAnalysisReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobAnalysisReportRepository extends JpaRepository<JobAnalysisReport, Integer> {

    List<JobAnalysisReport> findJobAnalysisReportsByStudentId(Integer studentId);

    JobAnalysisReport findJobAnalysisReportByJobAnalysisId(Integer jobAnalysisId);
}