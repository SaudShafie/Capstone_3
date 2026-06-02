package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiException;
import org.example.capstone_3.DTO.IN.ChallengeDTOIN;
import org.example.capstone_3.DTO.OUT.ChallengeDTOOUT;
import org.example.capstone_3.Model.Challenge;
import org.example.capstone_3.Model.Skill;
import org.example.capstone_3.Repository.ChallengeRepository;
import org.example.capstone_3.Repository.SkillRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final SkillRepository skillRepository;

    public ChallengeDTOOUT create(ChallengeDTOIN dto) {
        Challenge challenge = new Challenge();
        applyDto(challenge, dto);
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
        applyDto(challenge, dto);
        return toDtoOut(challengeRepository.save(challenge));
    }

    public void delete(Integer id) {
        Challenge challenge = challengeRepository.findChallengeById(id);
        if (challenge == null) {
            throw new ApiException("Challenge with id " + id + " not found");
        }
        challengeRepository.deleteById(id);
    }

    private void applyDto(Challenge challenge, ChallengeDTOIN dto) {
        challenge.setTitle(dto.getTitle());
        challenge.setQuestion(dto.getQuestion());
        challenge.setCorrectAnswer(dto.getCorrectAnswer());
        challenge.setPoints(dto.getPoints());
        challenge.setDifficulty(dto.getDifficulty());
        challenge.setSkill(findSkill(dto.getSkillId()));
    }

    private Skill findSkill(Integer skillId) {
        if (skillId == null) {
            return null;
        }
        Skill skill = skillRepository.findSkillById(skillId);
        if (skill == null) {
            throw new ApiException("Skill with id " + skillId + " not found");
        }
        return skill;
    }

    private ChallengeDTOOUT toDtoOut(Challenge challenge) {
        Integer skillId = challenge.getSkill() != null ? challenge.getSkill().getId() : null;
        return new ChallengeDTOOUT(
                challenge.getId(),
                challenge.getTitle(),
                challenge.getQuestion(),
                challenge.getCorrectAnswer(),
                challenge.getPoints(),
                challenge.getDifficulty(),
                skillId
        );
    }
}
