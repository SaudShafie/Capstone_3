package org.example.capstone_3.Repository;

import org.example.capstone_3.Model.RoadmapStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadmapStepRepository extends JpaRepository<RoadmapStep, Integer> {
    RoadmapStep findRoadmapStepById(Integer id);
}
