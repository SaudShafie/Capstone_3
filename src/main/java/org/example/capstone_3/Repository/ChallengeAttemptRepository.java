package org.example.capstone_3.Repository;

import org.example.capstone_3.Model.ChallengeAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeAttemptRepository extends JpaRepository<ChallengeAttempt, Integer> {

    ChallengeAttempt findChallengeAttemptById(Integer id);
}
