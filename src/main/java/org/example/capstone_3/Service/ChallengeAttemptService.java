package org.example.capstone_3.Service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiException;
import org.example.capstone_3.DTO.IN.ChallengeAttemptDTOIN;
import org.example.capstone_3.DTO.OUT.ChallengeAttemptDTOOUT;
import org.example.capstone_3.Model.Challenge;
import org.example.capstone_3.Model.ChallengeAttempt;
import org.example.capstone_3.Model.Student;
import org.example.capstone_3.Repository.ChallengeAttemptRepository;
import org.example.capstone_3.Repository.ChallengeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeAttemptService {

    private final ChallengeAttemptRepository challengeAttemptRepository;
    private final ChallengeRepository challengeRepository;
    private final EntityManager entityManager;

    public ChallengeAttemptDTOOUT create(ChallengeAttemptDTOIN dto) {
        ChallengeAttempt challengeAttempt = new ChallengeAttempt();
        applyDto(challengeAttempt, dto);
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
        applyDto(challengeAttempt, dto);
        return toDtoOut(challengeAttemptRepository.save(challengeAttempt));
    }

    public void delete(Integer id) {
        ChallengeAttempt challengeAttempt = challengeAttemptRepository.findChallengeAttemptById(id);
        if (challengeAttempt == null) {
            throw new ApiException("Challenge attempt with id " + id + " not found");
        }
        challengeAttemptRepository.deleteById(id);
    }

    private void applyDto(ChallengeAttempt challengeAttempt, ChallengeAttemptDTOIN dto) {
        challengeAttempt.setSubmittedAnswer(dto.getSubmittedAnswer());
        challengeAttempt.setCorrect(dto.getCorrect());
        challengeAttempt.setEarnedPoints(dto.getEarnedPoints());
        challengeAttempt.setSubmittedAt(dto.getSubmittedAt());
        challengeAttempt.setStudent(findStudent(dto.getStudentId()));
        challengeAttempt.setChallenge(findChallenge(dto.getChallengeId()));
    }

    private Student findStudent(Integer studentId) {
        if (studentId == null) {
            return null;
        }
        Student student = entityManager.find(Student.class, studentId);
        if (student == null) {
            throw new ApiException("Student with id " + studentId + " not found");
        }
        return student;
    }

    private Challenge findChallenge(Integer challengeId) {
        if (challengeId == null) {
            return null;
        }
        Challenge challenge = challengeRepository.findChallengeById(challengeId);
        if (challenge == null) {
            throw new ApiException("Challenge with id " + challengeId + " not found");
        }
        return challenge;
    }

    private ChallengeAttemptDTOOUT toDtoOut(ChallengeAttempt challengeAttempt) {
        Integer studentId = challengeAttempt.getStudent() != null ? challengeAttempt.getStudent().getId() : null;
        Integer challengeId = challengeAttempt.getChallenge() != null ? challengeAttempt.getChallenge().getId() : null;
        return new ChallengeAttemptDTOOUT(
                challengeAttempt.getId(),
                challengeAttempt.getSubmittedAnswer(),
                challengeAttempt.getCorrect(),
                challengeAttempt.getEarnedPoints(),
                challengeAttempt.getSubmittedAt(),
                studentId,
                challengeId
        );
    }
}
