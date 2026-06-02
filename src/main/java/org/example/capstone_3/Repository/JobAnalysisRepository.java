package org.example.capstone_3.Repository;

import org.example.capstone_3.Model.JobAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobAnalysisRepository extends JpaRepository<JobAnalysis, Integer> {

    List<JobAnalysis> findJobAnalysesByStudentId(Integer studentId);
}