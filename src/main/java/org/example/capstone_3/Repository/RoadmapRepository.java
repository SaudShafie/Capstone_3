package org.example.capstone_3.Repository;

import org.example.capstone_3.Model.Roadmap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadmapRepository extends JpaRepository<Roadmap,Integer> {
    Roadmap findRoadmapById(Integer id);
}
