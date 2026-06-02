package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiException;
import org.example.capstone_3.DTO.IN.ChallengeAttemptDTOIN;
import org.example.capstone_3.DTO.OUT.ChallengeAttemptDTOOUT;
import org.example.capstone_3.Model.ChallengeAttempt;
import org.example.capstone_3.Repository.ChallengeAttemptRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeAttemptService {

    private final ChallengeAttemptRepository challengeAttemptRepository;

    public ChallengeAttemptDTOOUT create(ChallengeAttemptDTOIN dto) {
        ChallengeAttempt challengeAttempt = new ChallengeAttempt();
        return toDtoOut(challengeAttemptRepository.save(challengeAttempt));
    }

    public ChallengeAttemptDTOOUT getById(Integer id) {
        ChallengeAttempt challengeAttempt = challengeAttemptRepository.findChallengeAttemptById(id);
        if (challengeAttempt == null) {
            throw new ApiException("Challenge attempt with id " + id + " not found");
        }
        return toDtoOut(challengeAttempt);
    }

    public List<ChallengeAttemptDTOOUT> getAll() {
        return challengeAttemptRepository.findAll().stream().map(this::toDtoOut).toList();
    }

    public ChallengeAttemptDTOOUT update(Integer id, ChallengeAttemptDTOIN dto) {
        ChallengeAttempt challengeAttempt = challengeAttemptRepository.findChallengeAttemptById(id);
        if (challengeAttempt == null) {
            throw new ApiException("Challenge attempt with id " + id + " not found");
        }
        return toDtoOut(challengeAttemptRepository.save(challengeAttempt));
    }

    public void delete(Integer id) {
        ChallengeAttempt challengeAttempt = challengeAttemptRepository.findChallengeAttemptById(id);
        if (challengeAttempt == null) {
            throw new ApiException("Challenge attempt with id " + id + " not found");
        }
        challengeAttemptRepository.deleteById(id);
    }

    private ChallengeAttemptDTOOUT toDtoOut(ChallengeAttempt challengeAttempt) {
        return new ChallengeAttemptDTOOUT(challengeAttempt.getId());
    }
}
