package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.AI.AiException;
import org.example.capstone_3.AI.AiService;
import org.example.capstone_3.Api.ApiException;
import org.example.capstone_3.DTO.IN.JobAnalysisDTOIn;
import org.example.capstone_3.DTO.OUT.JobAnalysisDTOOut;
import org.example.capstone_3.Model.JobAnalysis;
import org.example.capstone_3.Model.Skill;
import org.example.capstone_3.Model.Student;
import org.example.capstone_3.Repository.JobAnalysisRepository;
import org.example.capstone_3.Repository.SkillRepository;
import org.example.capstone_3.Repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobAnalysisService {

    private static final Pattern MATCH_SCORE_PATTERN =
            Pattern.compile("\"matchScore\"\\s*:\\s*(\\d+)");
    private static final Pattern QUOTED_STRING_PATTERN =
            Pattern.compile("\"((?:\\\\.|[^\"\\\\])*)\"");

    private final JobAnalysisRepository jobAnalysisRepository;
    private final StudentRepository studentRepository;
    private final SkillRepository skillRepository;
    private final StudentProfilePromptHelper studentProfilePromptHelper;
    private final AiService aiService;

    /**
     * Add job analysis: load student → AI analysis → save JobAnalysis.
     */
    public void addJobAnalysis(Integer studentId, JobAnalysisDTOIn dto) {
        Student student = studentRepository.findStudentById(studentId);
        if (student == null) {
            throw new ApiException("Student with id " + studentId + " not found");
        }

        AiJobAnalysisResult aiResult = fetchJobAnalysisFromAi(student, dto.getJobDescription());

        JobAnalysis jobAnalysis = new JobAnalysis();
        jobAnalysis.setJobDescription(dto.getJobDescription());
        applyAiResult(jobAnalysis, aiResult);
        jobAnalysis.setStudent(student);
        jobAnalysis.setCreatedAt(LocalDateTime.now());

        jobAnalysisRepository.save(jobAnalysis);
    }

    /**
     * Update job analysis: load record + student → AI analysis → save.
     */
    public void updateJobAnalysis(Integer id, JobAnalysisDTOIn dto) {
        JobAnalysis jobAnalysis = jobAnalysisRepository.findJobAnalysisById(id);
        if (jobAnalysis == null) {
            throw new ApiException("Job analysis with id " + id + " not found");
        }

        Student student = jobAnalysis.getStudent();
        if (student == null) {
            throw new ApiException("Student not found for job analysis " + id);
        }

        AiJobAnalysisResult aiResult = fetchJobAnalysisFromAi(student, dto.getJobDescription());

        jobAnalysis.setJobDescription(dto.getJobDescription());
        applyAiResult(jobAnalysis, aiResult);

        jobAnalysisRepository.save(jobAnalysis);
    }

    public void create(Integer studentId, JobAnalysisDTOIn dto) {
        addJobAnalysis(studentId, dto);
    }

    public JobAnalysisDTOOut getById(Integer id) {
        JobAnalysis jobAnalysis = jobAnalysisRepository.findJobAnalysisById(id);
        if (jobAnalysis == null) {
            throw new ApiException("Job analysis with id " + id + " not found");
        }
        return toDtoOut(jobAnalysis);
    }

    public List<JobAnalysisDTOOut> getAll() {
        List<JobAnalysisDTOOut> jobAnalysisDTOOuts = new ArrayList<>();
        for (JobAnalysis jobAnalysis : jobAnalysisRepository.findAll()) {
            jobAnalysisDTOOuts.add(toDtoOut(jobAnalysis));
        }
        return jobAnalysisDTOOuts;
    }

   public List<JobAnalysisDTOOut> getByStudentId(Integer studentId) {
       Student student = studentRepository.findStudentById(studentId);
       if (student == null) {
           throw new ApiException("Student with id " + studentId + " not found");
       }

       List<JobAnalysisDTOOut> jobAnalysisDTOOuts = new ArrayList<>();
       for (JobAnalysis jobAnalysis : jobAnalysisRepository.findJobAnalysesByStudentId(studentId)) {
           jobAnalysisDTOOuts.add(toDtoOut(jobAnalysis));
       }
       return jobAnalysisDTOOuts;
   }

    public void update(Integer id, JobAnalysisDTOIn dto) {
        updateJobAnalysis(id, dto);
    }

    public void delete(Integer id) {
        JobAnalysis jobAnalysis = jobAnalysisRepository.findJobAnalysisById(id);
        if (jobAnalysis == null) {
            throw new ApiException("Job analysis with id " + id + " not found");
        }
        jobAnalysisRepository.delete(jobAnalysis);
    }

    private AiJobAnalysisResult fetchJobAnalysisFromAi(Student student, String jobDescription) {
        String prompt = buildJobAnalysisPrompt(student, jobDescription);
        String json = aiService.ask(prompt);
        return parseJobAnalysisResult(json);
    }

    private String buildJobAnalysisPrompt(Student student, String jobDescription) {
        String studentSkillNames = formatStudentSkills(student);
        String availableSkillNames = formatAvailableSkills();
        String cv = studentProfilePromptHelper.formatCvForPrompt(student);
        String github = studentProfilePromptHelper.formatGithubForPrompt(student);

        return """
                You are a career coach. Compare the student profile to the job description.
                
                Respond with JSON only using this exact shape:
                {
                  "jobTitle": "inferred job title from the posting",
                  "matchScore": 0,
                  "missingSkills": "skills the student lacks for this role",
                  "summary": "2-4 sentence overview of fit",
                  "strengths": "student strengths relative to this job",
                  "weaknesses": "gaps or weaknesses relative to this job",
                  "skills": ["SkillName1", "SkillName2"]
                }
                matchScore must be an integer from 0 to 100.
                skills must be a JSON array of skill names picked ONLY from the available skills list below.
                Do NOT invent new skill names. If none apply, return an empty array [].
                Include skills the student has that match the job, and/or key job skills that exist in the available list.
                
                --- Available skills (use exact names only) ---
                %s
                
                --- Student ---
                Name: %s
                Major: %s
                Target role: %s
                Years of experience: %s
                Student skills: %s
                
                --- CV ---
                %s
                
                --- GitHub ---
                %s
                
                --- Job description ---
                %s
                """.formatted(
                availableSkillNames,
                student.getFullName(),
                student.getMajor(),
                student.getTargetRole(),
                student.getYearsExperience(),
                studentSkillNames,
                cv,
                github,
                jobDescription
        );
    }

    private String formatAvailableSkills() {
        List<Skill> skills = skillRepository.findAll();
        if (skills.isEmpty()) {
            return "(none in system — return empty skills array)";
        }
        return skills.stream()
                .map(Skill::getName)
                .collect(Collectors.joining(", "));
    }

    private String formatStudentSkills(Student student) {
        if (student.getSkills() == null || student.getSkills().isEmpty()) {
            return "(none listed)";
        }
        return student.getSkills().stream()
                .map(Skill::getName)
                .collect(Collectors.joining(", "));
    }

    private AiJobAnalysisResult parseJobAnalysisResult(String json) {
        int matchScore = parseMatchScore(json);
        String missingSkills = extractJsonString(json, "missingSkills");
        String summary = extractJsonString(json, "summary");
        String strengths = extractJsonString(json, "strengths");
        String weaknesses = extractJsonString(json, "weaknesses");
        String jobTitle = extractJsonString(json, "jobTitle");
        List<String> skillNames = extractJsonStringArray(json, "skills");

        if (missingSkills == null || summary == null || strengths == null || weaknesses == null) {
            throw new AiException("AI response is missing required job analysis fields.");
        }

        return new AiJobAnalysisResult(
                jobTitle == null || jobTitle.isBlank() ? "Job analysis" : jobTitle,
                matchScore,
                missingSkills,
                summary,
                strengths,
                weaknesses,
                skillNames
        );
    }

    private List<String> extractJsonStringArray(String json, String fieldName) {
        Pattern fieldPattern = Pattern.compile(
                "\"" + Pattern.quote(fieldName) + "\"\\s*:\\s*\\[(.*?)\\]",
                Pattern.DOTALL);
        Matcher arrayMatcher = fieldPattern.matcher(json);
        if (!arrayMatcher.find()) {
            return List.of();
        }

        List<String> values = new ArrayList<>();
        Matcher stringMatcher = QUOTED_STRING_PATTERN.matcher(arrayMatcher.group(1));
        while (stringMatcher.find()) {
            String value = unescapeJsonString(stringMatcher.group(1));
            if (!value.isBlank()) {
                values.add(value);
            }
        }
        return values;
    }

    private Set<Skill> resolveSkillsFromNames(List<String> skillNames) {
        Map<String, Skill> skillsInDatabase = skillRepository.findAll().stream()
                .collect(Collectors.toMap(
                        skill -> skill.getName().trim().toLowerCase(),
                        skill -> skill,
                        (existing, duplicate) -> existing
                ));

        Set<Skill> skills = new HashSet<>();
        for (String name : skillNames) {
            Skill skill = skillsInDatabase.get(name.trim().toLowerCase());
            if (skill != null) {
                skills.add(skill);
            }
            // Names not in the database are ignored — we never create new Skill rows here.
        }
        return skills;
    }

    private int parseMatchScore(String json) {
        Matcher matcher = MATCH_SCORE_PATTERN.matcher(json);
        if (!matcher.find()) {
            throw new AiException("AI response did not contain matchScore.");
        }
        int score = Integer.parseInt(matcher.group(1));
        if (score < 0 || score > 100) {
            throw new AiException("AI matchScore must be between 0 and 100.");
        }
        return score;
    }

    private String extractJsonString(String json, String fieldName) {
        Pattern pattern = Pattern.compile(
                "\"" + Pattern.quote(fieldName) + "\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"");
        Matcher matcher = pattern.matcher(json);
        if (!matcher.find()) {
            return null;
        }
        return unescapeJsonString(matcher.group(1));
    }

    private String unescapeJsonString(String value) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '\\' && i + 1 < value.length()) {
                char next = value.charAt(++i);
                switch (next) {
                    case 'n' -> result.append('\n');
                    case 'r' -> result.append('\r');
                    case 't' -> result.append('\t');
                    case '"' -> result.append('"');
                    case '\\' -> result.append('\\');
                    default -> result.append(next);
                }
            } else {
                result.append(c);
            }
        }
        return result.toString().trim();
    }

    private void applyAiResult(JobAnalysis jobAnalysis, AiJobAnalysisResult result) {
        jobAnalysis.setJobTitle(result.jobTitle());
        jobAnalysis.setMatchScore(result.matchScore());
        jobAnalysis.setMissingSkillsText(result.missingSkills());
        jobAnalysis.setRequiredSkillsText(result.strengths());
        jobAnalysis.setSummary(result.summary());
        jobAnalysis.setImprovements(result.weaknesses());
        jobAnalysis.setRecommendations(formatRecommendations(result.summary(), result.weaknesses()));
        jobAnalysis.setSkills(resolveSkillsFromNames(result.skillNames()));
    }

    private String formatRecommendations(String summary, String weaknesses) {
        return "Summary: " + summary + "\n\nWeaknesses: " + weaknesses;
    }

    private JobAnalysisDTOOut toDtoOut(JobAnalysis jobAnalysis) {
        return new JobAnalysisDTOOut(
                jobAnalysis.getId(),
                jobAnalysis.getJobTitle(),
                jobAnalysis.getJobDescription(),
                jobAnalysis.getRequiredSkillsText(),
                jobAnalysis.getMissingSkillsText(),
                jobAnalysis.getMatchScore(),
                jobAnalysis.getSummary(),
                jobAnalysis.getImprovements(),
                jobAnalysis.getRecommendations()
        );
    }

    //private StudentSummaryDTOOut mapStudent(Student student) {
    //    if (student == null) {
    //        return null;
    //    }
    //    return new StudentSummaryDTOOut(
    //            student.getId(),
    //            student.getFullName(),
    //            student.getEmail(),
    //            student.getMajor(),
    //            student.getTargetRole(),
    //            student.getReadinessScore()
    //    );
    //}

    //private Set<SkillDTOOut> mapSkills(Set<Skill> skills) {
    //    Set<SkillDTOOut> skillDTOOuts = new HashSet<>();
    //    if (skills == null) {
    //        return skillDTOOuts;
    //    }
    //    for (Skill skill : skills) {
    //        skillDTOOuts.add(new SkillDTOOut(skill.getId(), skill.getName(), skill.getCategory()));
    //    }
    //    return skillDTOOuts;
    //}

    private record AiJobAnalysisResult(
            String jobTitle,
            int matchScore,
            String missingSkills,
            String summary,
            String strengths,
            String weaknesses,
            List<String> skillNames
    ) {
    }
}
