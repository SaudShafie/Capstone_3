package org.example.capstone_3.Repository;

import org.example.capstone_3.Model.LearningGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LearningGroupRepository extends JpaRepository<LearningGroup,Integer> {
    LearningGroup findLearningGroupById(Integer id);
}
