package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.AI.AiException;
import org.example.capstone_3.AI.AiService;
import org.example.capstone_3.Api.ApiException;
import org.example.capstone_3.DTO.IN.ChallengeDTOIN;
import org.example.capstone_3.DTO.OUT.ChallengeDTOOUT;
import org.example.capstone_3.Model.Challenge;
import org.example.capstone_3.Model.Skill;
import org.example.capstone_3.Repository.ChallengeRepository;
import org.example.capstone_3.Repository.SkillRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private static final Pattern TITLE_PATTERN =
            Pattern.compile("\"title\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern QUESTION_PATTERN =
            Pattern.compile("\"question\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern CORRECT_ANSWER_PATTERN =
            Pattern.compile("\"correctAnswer\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern POINTS_PATTERN =
            Pattern.compile("\"points\"\\s*:\\s*(\\d+)");
    private static final Pattern DIFFICULTY_PATTERN =
            Pattern.compile("\"difficulty\"\\s*:\\s*\"(EASY|MEDIUM|HARD)\"");

    private final ChallengeRepository challengeRepository;
    private final SkillRepository skillRepository;
    private final AiService aiService;

    public void create(ChallengeDTOIN dto) {
        Skill skill = findSkillByName(dto.getSkillName());

        Challenge challenge = fetchChallengeFromAi(dto.getSkillName());

        challenge.setSkill(skill);
        applyDto(challenge, skill.getId());

        challengeRepository.save(challenge);
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

    public void update(Integer id, ChallengeDTOIN dto) {
        Challenge challenge = challengeRepository.findChallengeById(id);
        if (challenge == null) {
            throw new ApiException("Challenge with id " + id + " not found");
        }
        Skill skill = findSkillByName(dto.getSkillName());
        Challenge updatedChallenge = fetchChallengeFromAi(dto.getSkillName());

        updatedChallenge.setId(challenge.getId());
        updatedChallenge.setSkill(skill);

        challengeRepository.save(updatedChallenge);
    }

    public void delete(Integer id) {
        Challenge challenge = challengeRepository.findChallengeById(id);
        if (challenge == null) {
            throw new ApiException("Challenge with id " + id + " not found");
        }
        challengeRepository.deleteById(id);
    }

    private void applyDto(Challenge challenge, Integer skillId) {
        challenge.setSkill(findSkill(skillId));
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

    private Skill findSkillByName(String skillName){
        if (skillName == null) {
            return null;
        }
        Skill skill = skillRepository.findSkillByName(skillName);
        if (skill == null) {
            throw new ApiException("Skill with name " + skillName + " not found");
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

    // AI service

    private Challenge fetchChallengeFromAi(String skillName) {
        String prompt = buildChallengePrompt(skillName);
        String json = aiService.ask(prompt);
        return parseChallengeJson(json);
    }

    private String buildChallengePrompt(String skillName) {
        return """
            You are an expert challenge designer for a professional career development platform.
            Your job is to create high-quality, realistic challenges that test real-world understanding
            across any field or domain — technical, business, creative, or otherwise.
            
            Generate a single challenge for the skill provided below.
            The challenge must be specific, practical, and appropriate for professionals in that field.
            
            Respond with JSON only using this exact shape:
            {
              "title": "...",
              "question": "...",
              "correctAnswer": "...",
              "points": 0,
              "difficulty": "EASY"
            }
            
            Constraints:
            - title: concise and specific to the skill (max 10 words)
            - question: a clear, real-world scenario or problem — make it specific to %s and how it is used professionally
            - correctAnswer: the precise, professional-level answer expected — must be unambiguous
            - points: integer between 10 and 100 — EASY=10-40, MEDIUM=41-70, HARD=71-100
            - difficulty: must be exactly one of: EASY, MEDIUM, HARD — choose based on question complexity
            
            Rules:
            - The challenge must reflect actual real-world usage of the skill in a professional context
            - Never generate trivial, vague, or overly academic questions
            - The correct answer must be accurate and directly tied to the skill
            
            Skill: %s
            """.formatted(skillName, skillName);
    }

    private Challenge parseChallengeJson(String json) {
        Matcher titleMatcher = TITLE_PATTERN.matcher(json);
        Matcher questionMatcher = QUESTION_PATTERN.matcher(json);
        Matcher correctAnswerMatcher = CORRECT_ANSWER_PATTERN.matcher(json);
        Matcher pointsMatcher = POINTS_PATTERN.matcher(json);
        Matcher difficultyMatcher = DIFFICULTY_PATTERN.matcher(json);

        if (!titleMatcher.find()) throw new AiException("AI response did not contain title.");
        if (!questionMatcher.find()) throw new AiException("AI response did not contain question.");
        if (!correctAnswerMatcher.find()) throw new AiException("AI response did not contain correctAnswer.");
        if (!pointsMatcher.find()) throw new AiException("AI response did not contain points.");
        if (!difficultyMatcher.find()) throw new AiException("AI response did not contain difficulty.");

        Challenge challenge = new Challenge();
        challenge.setTitle(titleMatcher.group(1));
        challenge.setQuestion(questionMatcher.group(1));
        challenge.setCorrectAnswer(correctAnswerMatcher.group(1));
        challenge.setPoints(Integer.parseInt(pointsMatcher.group(1)));
        challenge.setDifficulty(difficultyMatcher.group(1));
        return challenge;
    }
}
