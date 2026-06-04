package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.AI.AiException;
import org.example.capstone_3.AI.AiService;
import org.example.capstone_3.Api.ApiException;
import org.example.capstone_3.DTO.IN.ChallengeAttemptDTOIN;
import org.example.capstone_3.DTO.OUT.ChallengeAttemptDTOOUT;
import org.example.capstone_3.Model.Challenge;
import org.example.capstone_3.Model.ChallengeAttempt;
import org.example.capstone_3.Model.Student;
import org.example.capstone_3.Repository.ChallengeAttemptRepository;
import org.example.capstone_3.Repository.ChallengeRepository;
import org.example.capstone_3.Repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ChallengeAttemptService {

    private static final Pattern IS_CORRECT_PATTERN =
            Pattern.compile("\"isCorrect\"\\s*:\\s*(true|false)");

    private final ChallengeAttemptRepository challengeAttemptRepository;
    private final ChallengeRepository challengeRepository;
    private final StudentRepository studentRepository;
    private final AiService aiService;

    public void create(Integer student_id, Integer challenge_id, ChallengeAttemptDTOIN dto) {
        Student student = findStudent(student_id);
        Challenge challenge = findChallenge(challenge_id);

        ChallengeAttempt correctAttempt = challengeAttemptRepository.correctAttemptForChallenge(student_id, challenge_id);
        if(correctAttempt != null){
            throw new ApiException("Already solved this challenge");
        }

        boolean isCorrect = fetchIsCorrectFromAi(dto.getSubmittedAnswer(), challenge.getCorrectAnswer());

        ChallengeAttempt challengeAttempt = new ChallengeAttempt();
        applyDto(challengeAttempt, dto);
        challengeAttempt.setStudent(student);
        challengeAttempt.setChallenge(challenge);
        challengeAttempt.setCorrect(isCorrect);
        challengeAttempt.setSubmittedAt(LocalDateTime.now());

        if (isCorrect && challenge.getDeadline().isBefore(LocalDateTime.now())) {
            student.setXp(student.getXp() + challenge.getPoints());
            studentRepository.save(student);
        }

        challengeAttemptRepository.save(challengeAttempt);
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

    //Useless
    public void update(Integer id, ChallengeAttemptDTOIN dto) {
        ChallengeAttempt challengeAttempt = challengeAttemptRepository.findChallengeAttemptById(id);
        if (challengeAttempt == null) {
            throw new ApiException("Challenge attempt with id " + id + " not found");
        }

        Challenge challenge = challengeAttempt.getChallenge();
        Student student = challengeAttempt.getStudent();

        boolean isCorrect = fetchIsCorrectFromAi(dto.getSubmittedAnswer(), challenge.getCorrectAnswer());

        // reverse old points if previous attempt was correct
        if (challengeAttempt.getCorrect()) {
            student.setXp(student.getXp() - challenge.getPoints());
        }

        challengeAttempt.setSubmittedAnswer(dto.getSubmittedAnswer());
        challengeAttempt.setSubmittedAt(LocalDateTime.now());
        challengeAttempt.setCorrect(isCorrect);

        if (isCorrect) {
            student.setXp(student.getXp() + challenge.getPoints());
        }

        studentRepository.save(student);
        challengeAttemptRepository.save(challengeAttempt);
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
    }


    private Student findStudent(Integer studentId) {
        if (studentId == null) {
            return null;
        }
        Student student = studentRepository.findStudentById(studentId);
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
                challengeAttempt.getSubmittedAt()
        );
    }

    // AI service

    private boolean fetchIsCorrectFromAi(String submittedAnswer, String correctAnswer) {
        String prompt = buildIsCorrectPrompt(submittedAnswer, correctAnswer);
        String json = aiService.ask(prompt);
        return parseIsCorrect(json);
    }

    private String buildIsCorrectPrompt(String submittedAnswer, String correctAnswer) {
        return """
            You are a professional answer evaluator for a career development platform.
            Your job is to compare a student's submitted answer against the correct answer
            and determine whether the student's answer is correct.
            
            Be flexible with wording — if the meaning and core concept match, consider it correct.
            Do not penalize for minor spelling differences or different phrasing of the same idea.
            
            Respond with JSON only using this exact shape:
            {"isCorrect": false}
            
            Constraints:
            - isCorrect: must be exactly true or false
            
            Correct Answer: %s
            Student Answer: %s
            """.formatted(correctAnswer, submittedAnswer);
    }

    private boolean parseIsCorrect(String json) {
        Matcher matcher = IS_CORRECT_PATTERN.matcher(json);
        if (!matcher.find()) {
            throw new AiException("AI response did not contain isCorrect.");
        }
        return Boolean.parseBoolean(matcher.group(1));
    }
}
