package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.AI.AiException;
import org.example.capstone_3.AI.AiService;
import org.example.capstone_3.Api.ApiException;
import org.example.capstone_3.DTO.IN.MockInterviewDTOIN;
import org.example.capstone_3.DTO.OUT.MockInterviewDTOOUT;
import org.example.capstone_3.Model.JobAnalysis;
import org.example.capstone_3.Model.Mentor;
import org.example.capstone_3.Model.MockInterview;
import org.example.capstone_3.Model.Student;
import org.example.capstone_3.Repository.JobAnalysisRepository;
import org.example.capstone_3.Repository.MentorRepository;
import org.example.capstone_3.Repository.MockInterviewRepository;
import org.example.capstone_3.Repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MockInterviewService {

    private static final Pattern QUESTIONS_PATTERN =
            Pattern.compile("\"questions\"\\s*:\\s*\"(.*?)\"", Pattern.DOTALL);

    private final MockInterviewRepository mockInterviewRepository;
    private final StudentRepository studentRepository;
    private final MentorRepository mentorRepository;
    private final JobAnalysisRepository jobAnalysisRepository;
    private final AiService aiService;

    public void create(Integer studentId, Integer mentorId, MockInterviewDTOIN dto) {

        Student student = studentRepository.findStudentById(studentId);

        if (student == null) {
            throw new ApiException("Student with id " + studentId + " not found");
        }

        Mentor mentor = mentorRepository.findMentorById(mentorId);

        if (mentor == null) {
            throw new ApiException("Mentor with id " + mentorId + " not found");
        }

        if (!Boolean.TRUE.equals(mentor.getAcceptedByAdmin())) {
            throw new ApiException("Mentor is not accepted by admin yet");
        }

        if (!Boolean.TRUE.equals(mentor.getAvailable())) {
            throw new ApiException("Mentor is not available");
        }

        MockInterview mockInterview = new MockInterview();

        mockInterview.setInterviewType(dto.getInterviewType());
        mockInterview.setDescription(dto.getDescription());
        mockInterview.setScheduledAt(dto.getScheduledAt());
        mockInterview.setUrl(dto.getUrl());

        mockInterview.setStatus("PENDING");
        mockInterview.setCreatedAt(LocalDateTime.now());
        mockInterview.setStudent(student);
        mockInterview.setMentor(mentor);
        mockInterview.setStudentAnswers(null);
        mockInterview.setFeedback(null);
        mockInterview.setScore(null);

        String questions = generateQuestionsFromAi(student, mentor, dto, null);
        mockInterview.setQuestions(questions);

        mockInterviewRepository.save(mockInterview);
    }

    public void createWithJobAnalysis(Integer studentId, Integer mentorId, Integer jobAnalysisId, MockInterviewDTOIN dto) {

        Student student = studentRepository.findStudentById(studentId);

        if (student == null) {
            throw new ApiException("Student with id " + studentId + " not found");
        }

        Mentor mentor = mentorRepository.findMentorById(mentorId);

        if (mentor == null) {
            throw new ApiException("Mentor with id " + mentorId + " not found");
        }

        if (!Boolean.TRUE.equals(mentor.getAcceptedByAdmin())) {
            throw new ApiException("Mentor is not accepted by admin yet");
        }

        if (!Boolean.TRUE.equals(mentor.getAvailable())) {
            throw new ApiException("Mentor is not available");
        }

        JobAnalysis jobAnalysis = jobAnalysisRepository.findJobAnalysisById(jobAnalysisId);

        if (jobAnalysis == null) {
            throw new ApiException("Job analysis with id " + jobAnalysisId + " not found");
        }

        if (jobAnalysis.getStudent() == null || !jobAnalysis.getStudent().getId().equals(studentId)) {
            throw new ApiException("This job analysis does not belong to the student");
        }

        MockInterview mockInterview = new MockInterview();

        mockInterview.setInterviewType(dto.getInterviewType());
        mockInterview.setDescription(dto.getDescription());
        mockInterview.setScheduledAt(dto.getScheduledAt());
        mockInterview.setUrl(dto.getUrl());

        mockInterview.setStatus("PENDING");
        mockInterview.setCreatedAt(LocalDateTime.now());
        mockInterview.setStudent(student);
        mockInterview.setMentor(mentor);
        mockInterview.setJobAnalysis(jobAnalysis);
        mockInterview.setStudentAnswers(null);
        mockInterview.setFeedback(null);
        mockInterview.setScore(null);

        String questions = generateQuestionsFromAi(student, mentor, dto, jobAnalysis);
        mockInterview.setQuestions(questions);

        mockInterviewRepository.save(mockInterview);
    }

    public MockInterviewDTOOUT getById(Integer id) {

        MockInterview mockInterview = mockInterviewRepository.findMockInterviewById(id);

        if (mockInterview == null) {
            throw new ApiException("Mock interview with id " + id + " not found");
        }

        return toDtoOut(mockInterview);
    }

    public List<MockInterviewDTOOUT> getAll() {

        List<MockInterview> mockInterviews = mockInterviewRepository.findAll();

        List<MockInterviewDTOOUT> mockInterviewDTOOUTS = new ArrayList<>();

        for (MockInterview mockInterview : mockInterviews) {
            mockInterviewDTOOUTS.add(toDtoOut(mockInterview));
        }

        return mockInterviewDTOOUTS;
    }

    public List<MockInterviewDTOOUT> getPendingMockInterviews(Integer mentorId) {

        Mentor mentor = mentorRepository.findMentorById(mentorId);

        if (mentor == null) {
            throw new ApiException("Mentor with id " + mentorId + " not found");
        }

        List<MockInterview> mockInterviews =
                mockInterviewRepository.findMockInterviewsByMentorIdAndStatus(mentorId, "PENDING");

        List<MockInterviewDTOOUT> mockInterviewDTOOUTS = new ArrayList<>();

        for (MockInterview mockInterview : mockInterviews) {
            mockInterviewDTOOUTS.add(toDtoOut(mockInterview));
        }

        return mockInterviewDTOOUTS;
    }

    public void acceptMockInterview(Integer mentorId, Integer mockInterviewId) {

        Mentor mentor = mentorRepository.findMentorById(mentorId);

        if (mentor == null) {
            throw new ApiException("Mentor with id " + mentorId + " not found");
        }

        MockInterview mockInterview = mockInterviewRepository.findMockInterviewById(mockInterviewId);

        if (mockInterview == null) {
            throw new ApiException("Mock interview with id " + mockInterviewId + " not found");
        }

        if (mockInterview.getMentor() == null || !mockInterview.getMentor().getId().equals(mentorId)) {
            throw new ApiException("This mock interview does not belong to this mentor");
        }

        if (!mockInterview.getStatus().equals("PENDING")) {
            throw new ApiException("Only pending mock interviews can be accepted");
        }

        mockInterview.setStatus("SCHEDULE");

        mockInterviewRepository.save(mockInterview);
    }

    public void rejectMockInterview(Integer mentorId, Integer mockInterviewId) {

        Mentor mentor = mentorRepository.findMentorById(mentorId);

        if (mentor == null) {
            throw new ApiException("Mentor with id " + mentorId + " not found");
        }

        MockInterview mockInterview = mockInterviewRepository.findMockInterviewById(mockInterviewId);

        if (mockInterview == null) {
            throw new ApiException("Mock interview with id " + mockInterviewId + " not found");
        }

        if (mockInterview.getMentor() == null || !mockInterview.getMentor().getId().equals(mentorId)) {
            throw new ApiException("This mock interview does not belong to this mentor");
        }

        if (!mockInterview.getStatus().equals("PENDING")) {
            throw new ApiException("Only pending mock interviews can be rejected");
        }

        mockInterview.setStatus("REJECT");

        mockInterviewRepository.save(mockInterview);
    }

    public void completeMockInterview(Integer mentorId, Integer mockInterviewId, String feedback, Integer score) {

        Mentor mentor = mentorRepository.findMentorById(mentorId);

        if (mentor == null) {
            throw new ApiException("Mentor with id " + mentorId + " not found");
        }

        MockInterview mockInterview = mockInterviewRepository.findMockInterviewById(mockInterviewId);

        if (mockInterview == null) {
            throw new ApiException("Mock interview with id " + mockInterviewId + " not found");
        }

        if (mockInterview.getMentor() == null || !mockInterview.getMentor().getId().equals(mentorId)) {
            throw new ApiException("This mock interview does not belong to this mentor");
        }

        if (!mockInterview.getStatus().equals("SCHEDULE")) {
            throw new ApiException("Only scheduled mock interviews can be completed");
        }

        if (score == null || score < 0 || score > 100) {
            throw new ApiException("Score must be between 0 and 100");
        }

        mockInterview.setFeedback(feedback);
        mockInterview.setScore(score);
        mockInterview.setStatus("COMPLETE");

        mockInterviewRepository.save(mockInterview);
    }

    public void update(Integer id, MockInterviewDTOIN dto) {

        MockInterview mockInterview = mockInterviewRepository.findMockInterviewById(id);

        if (mockInterview == null) {
            throw new ApiException("Mock interview with id " + id + " not found");
        }

        if (mockInterview.getStatus().equals("COMPLETE")) {
            throw new ApiException("Completed mock interview cannot be updated");
        }

        mockInterview.setInterviewType(dto.getInterviewType());
        mockInterview.setDescription(dto.getDescription());
        mockInterview.setScheduledAt(dto.getScheduledAt());
        mockInterview.setUrl(dto.getUrl());

        String questions = generateQuestionsFromAi(
                mockInterview.getStudent(),
                mockInterview.getMentor(),
                dto,
                mockInterview.getJobAnalysis()
        );

        mockInterview.setQuestions(questions);

        mockInterviewRepository.save(mockInterview);
    }

    public void delete(Integer id) {

        MockInterview mockInterview = mockInterviewRepository.findMockInterviewById(id);

        if (mockInterview == null) {
            throw new ApiException("Mock interview with id " + id + " not found");
        }

        mockInterviewRepository.delete(mockInterview);
    }

    private String generateQuestionsFromAi(Student student, Mentor mentor, MockInterviewDTOIN dto, JobAnalysis jobAnalysis) {

        String prompt = buildInterviewQuestionsPrompt(student, mentor, dto, jobAnalysis);

        String json = aiService.ask(prompt);

        return parseQuestions(json);
    }

    private String buildInterviewQuestionsPrompt(Student student, Mentor mentor, MockInterviewDTOIN dto, JobAnalysis jobAnalysis) {

        String cv = student.getCvText() == null || student.getCvText().isBlank()
                ? "(no CV provided)"
                : student.getCvText();

        String jobAnalysisText = "(no job analysis provided)";

        if (jobAnalysis != null) {
            jobAnalysisText = """
                    Job title: %s
                    Job description: %s
                    Required skills: %s
                    Missing skills: %s
                    Match score: %s
                    """.formatted(
                    jobAnalysis.getJobTitle(),
                    jobAnalysis.getJobDescription(),
                    jobAnalysis.getRequiredSkillsText(),
                    jobAnalysis.getMissingSkillsText(),
                    jobAnalysis.getMatchScore()
            );
        }

        return """
                You are helping a mentor prepare a mock interview for a student.
                
                Respond with JSON only using this exact shape:
                {"questions": "Question 1...\\nQuestion 2...\\nQuestion 3...\\nQuestion 4...\\nQuestion 5..."}
                
                Generate 10 interview questions.
                Questions must match the interview type, student profile, mentor specialization, and interview description.
                
                Interview type: %s
                Interview description: %s
                
                Student name: %s
                Student major: %s
                Student target role: %s
                Student years of experience: %s
                Student readiness score: %s
                
                Mentor job title: %s
                Mentor specialization: %s
                Mentor years of experience: %s
                
                --- Student CV ---
                %s
                
                --- Job Analysis ---
                %s
                """.formatted(
                dto.getInterviewType(),
                dto.getDescription(),
                student.getFullName(),
                student.getMajor(),
                student.getTargetRole(),
                student.getYearsExperience(),
                student.getReadinessScore(),
                mentor.getJobTitle(),
                mentor.getSpecialization(),
                mentor.getYearsExperience(),
                cv,
                jobAnalysisText
        );
    }

    private String parseQuestions(String json) {

        Matcher matcher = QUESTIONS_PATTERN.matcher(json);

        if (!matcher.find()) {
            throw new AiException("AI response did not contain questions.");
        }

        String questions = matcher.group(1)
                .replace("\\n", "\n")
                .replace("\\\"", "\"");

        if (questions.isBlank()) {
            throw new AiException("AI returned empty interview questions.");
        }

        return questions;
    }

    private MockInterviewDTOOUT toDtoOut(MockInterview mockInterview) {

        Integer studentId = mockInterview.getStudent() != null ? mockInterview.getStudent().getId() : null;

        Integer mentorId = mockInterview.getMentor() != null ? mockInterview.getMentor().getId() : null;

        Integer jobAnalysisId = mockInterview.getJobAnalysis() != null ? mockInterview.getJobAnalysis().getId() : null;

        return new MockInterviewDTOOUT(
                mockInterview.getId(),
                mockInterview.getInterviewType(),
                mockInterview.getScheduledAt(),
                mockInterview.getStatus(),
                mockInterview.getQuestions(),
                mockInterview.getStudentAnswers(),
                mockInterview.getFeedback(),
                mockInterview.getScore(),
                mockInterview.getUrl(),
                mockInterview.getCreatedAt(),
                studentId,
                mentorId,
                jobAnalysisId
        );
    }
}