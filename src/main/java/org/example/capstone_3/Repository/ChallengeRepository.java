package org.example.capstone_3.Repository;

import org.example.capstone_3.Model.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeRepository extends JpaRepository<Challenge, Integer> {

    Challenge findChallengeById(Integer id);
}
