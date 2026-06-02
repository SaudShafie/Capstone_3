package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiException;
import org.example.capstone_3.DTO.IN.ChallengeDTOIN;
import org.example.capstone_3.DTO.OUT.ChallengeDTOOUT;
import org.example.capstone_3.Model.Challenge;
import org.example.capstone_3.Repository.ChallengeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    public ChallengeDTOOUT create(ChallengeDTOIN dto) {
        Challenge challenge = new Challenge();
        return toDtoOut(challengeRepository.save(challenge));
    }

    public ChallengeDTOOUT getById(Integer id) {
        Challenge challenge = challengeRepository.findChallengeById(id);
        if (challenge == null) {
            throw new ApiException("Challenge with id " + id + " not found");
        }
        return toDtoOut(challenge);
    }

    public List<ChallengeDTOOUT> getAll() {
        return challengeRepository.findAll().stream().map(this::toDtoOut).toList();
    }

    public ChallengeDTOOUT update(Integer id, ChallengeDTOIN dto) {
        Challenge challenge = challengeRepository.findChallengeById(id);
        if (challenge == null) {
            throw new ApiException("Challenge with id " + id + " not found");
        }
        return toDtoOut(challengeRepository.save(challenge));
    }

    public void delete(Integer id) {
        Challenge challenge = challengeRepository.findChallengeById(id);
        if (challenge == null) {
            throw new ApiException("Challenge with id " + id + " not found");
        }
        challengeRepository.deleteById(id);
    }

    private ChallengeDTOOUT toDtoOut(Challenge challenge) {
        return new ChallengeDTOOUT(challenge.getId());
    }
}
