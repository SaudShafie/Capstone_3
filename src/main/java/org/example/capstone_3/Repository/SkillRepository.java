package org.example.capstone_3.Repository;

import org.example.capstone_3.Model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Integer> {

    Skill findSkillById(Integer id);

    Skill findSkillByName(String name);

    List<Skill> findSkillsByCategoryContainingIgnoreCase(String category);

    List<Skill> findSkillsByNameContainingIgnoreCase(String name);
}