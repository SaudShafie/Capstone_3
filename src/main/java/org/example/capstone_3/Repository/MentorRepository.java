package org.example.capstone_3.Repository;

import org.example.capstone_3.Model.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MentorRepository extends JpaRepository<Mentor, Integer> {

    Mentor findMentorById(Integer id);
}
