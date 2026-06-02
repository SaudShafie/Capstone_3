package org.example.capstone_3.Repository;

import org.example.capstone_3.Model.JobAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobAnalysisRepository extends JpaRepository<JobAnalysis, Integer> {

    JobAnalysis findJobAnalysisById(Integer id);
}
