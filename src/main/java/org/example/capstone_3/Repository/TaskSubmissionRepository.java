package org.example.capstone_3.Repository;

import org.example.capstone_3.Model.TaskSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskSubmissionRepository extends JpaRepository<TaskSubmission,Integer> {
    TaskSubmission findTaskSubmissionById(Integer id);
}
