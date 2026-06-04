package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.AI.AiException;
import org.example.capstone_3.AI.AiService;
import org.example.capstone_3.Api.ApiException;
import org.example.capstone_3.DTO.IN.AiInterviewAnswerDTOIN;
import org.example.capstone_3.DTO.IN.AiMockInterviewDTOIN;
import org.example.capstone_3.DTO.IN.MockInterviewDTOIN;
import org.example.capstone_3.DTO.OUT.MockInterviewDTOOUT;
import org.example.capstone_3.Model.Mentor;
import org.example.capstone_3.Model.MockInterview;
import org.example.capstone_3.Model.Student;
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

    private static final Pattern FEEDBACK_PATTERN =
            Pattern.compile("\"feedback\"\\s*:\\s*\"(.*?)\"", Pattern.DOTALL);

    private static final Pattern SCORE_PATTERN =
            Pattern.compile("\"score\"\\s*:\\s*(\\d+)");

    private final MockInterviewRepository mockInterviewRepository;
    private final StudentRepository studentRepository;
    private final MentorRepository mentorRepository;
    private final AiService aiService;
    private final MeetingService meetingService;
    private final EmailService emailService;

    // =========================
    // MENTOR FLOW
    // =========================

    public void createMentorInterview(Integer studentId, Integer mentorId, MockInterviewDTOIN dto) {

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

        mockInterview.setInterviewMode("MENTOR");
        mockInterview.setInterviewType(dto.getInterviewType());
        mockInterview.setDescription(dto.getDescription());
        mockInterview.setScheduledAt(dto.getScheduledAt());
        mockInterview.setDurationMinutes(dto.getDurationMinutes());
        mockInterview.setStatus("PENDING");
        mockInterview.setCreatedAt(LocalDateTime.now());
        mockInterview.setStudent(student);
        mockInterview.setMentor(mentor);

        mockInterview.setQuestions(generateMentorQuestions(student, mentor, dto));
        mockInterview.setStudentAnswers(null);
        mockInterview.setFeedback(null);
        mockInterview.setScore(null);
        mockInterview.setUrl(null);
        mockInterview.setMeetingProvider(null);
        mockInterview.setExternalMeetingId(null);

        mockInterviewRepository.save(mockInterview);
    }

    public void acceptMentorInterview(Integer mentorId, Integer mockInterviewId) {

        Mentor mentor = mentorRepository.findMentorById(mentorId);

        if (mentor == null) {
            throw new ApiException("Mentor with id " + mentorId + " not found");
        }

        MockInterview mockInterview = mockInterviewRepository.findMockInterviewById(mockInterviewId);

        if (mockInterview == null) {
            throw new ApiException("Mock interview with id " + mockInterviewId + " not found");
        }

        if (!mockInterview.getInterviewMode().equals("MENTOR")) {
            throw new ApiException("Only mentor interviews can be accepted by mentor");
        }

        if (mockInterview.getMentor() == null || !mockInterview.getMentor().getId().equals(mentorId)) {
            throw new ApiException("This mock interview does not belong to this mentor");
        }

        if (!mockInterview.getStatus().equals("PENDING")) {
            throw new ApiException("Only pending interviews can be accepted");
        }

        MeetingResponse meeting = meetingService.createMeeting(
                mockInterview,
                mockInterview.getStudent(),
                mentor
        );

        mockInterview.setUrl(meeting.getJoinUrl());
        mockInterview.setExternalMeetingId(meeting.getMeetingId());
        mockInterview.setMeetingProvider(meeting.getProvider());
        mockInterview.setStatus("SCHEDULE");

        mockInterviewRepository.save(mockInterview);

        emailService.sendMentorInterviewScheduledEmail(
                mockInterview.getStudent(),
                mentor,
                mockInterview
        );
    }

    public List<MockInterviewDTOOUT> getPendingMentorInterviews(Integer mentorId) {

        Mentor mentor = mentorRepository.findMentorById(mentorId);

        if (mentor == null) {
            throw new ApiException("Mentor with id " + mentorId + " not found");
        }

        List<MockInterview> mockInterviews =
                mockInterviewRepository.findMockInterviewsByMentorIdAndStatus(mentorId, "PENDING");

        List<MockInterviewDTOOUT> dtoOuts = new ArrayList<>();

        for (MockInterview mockInterview : mockInterviews) {
            if (mockInterview.getInterviewMode().equals("MENTOR")) {
                dtoOuts.add(toDtoOut(mockInterview));
            }
        }

        return dtoOuts;
    }

    public void completeMentorInterview(Integer mentorId, Integer mockInterviewId, String feedback, Integer score) {

        Mentor mentor = mentorRepository.findMentorById(mentorId);

        if (mentor == null) {
            throw new ApiException("Mentor with id " + mentorId + " not found");
        }

        MockInterview mockInterview = mockInterviewRepository.findMockInterviewById(mockInterviewId);

        if (mockInterview == null) {
            throw new ApiException("Mock interview with id " + mockInterviewId + " not found");
        }

        if (!mockInterview.getInterviewMode().equals("MENTOR")) {
            throw new ApiException("Only mentor interviews can be completed by mentor");
        }

        if (mockInterview.getMentor() == null || !mockInterview.getMentor().getId().equals(mentorId)) {
            throw new ApiException("This mock interview does not belong to this mentor");
        }

        if (!mockInterview.getStatus().equals("SCHEDULE")) {
            throw new ApiException("Only scheduled interviews can be completed");
        }

        if (score == null || score < 0 || score > 100) {
            throw new ApiException("Score must be between 0 and 100");
        }

        mockInterview.setFeedback(feedback);
        mockInterview.setScore(score);
        mockInterview.setStatus("COMPLETE");

        mockInterviewRepository.save(mockInterview);
    }

    // =========================
    // AI FLOW
    // =========================

    public void createAiInterview(Integer studentId, AiMockInterviewDTOIN dto) {

        Student student = studentRepository.findStudentById(studentId);

        if (student == null) {
            throw new ApiException("Student with id " + studentId + " not found");
        }

        MockInterview mockInterview = new MockInterview();

        mockInterview.setInterviewMode("AI");
        mockInterview.setInterviewType(dto.getInterviewType());
        mockInterview.setDescription(dto.getDescription());

        mockInterview.setScheduledAt(LocalDateTime.now());
        mockInterview.setDurationMinutes(0);
        mockInterview.setStatus("SCHEDULE");
        mockInterview.setCreatedAt(LocalDateTime.now());

        mockInterview.setStudent(student);
        mockInterview.setMentor(null);
        mockInterview.setJobAnalysis(null);

        mockInterview.setQuestions(generateAiInterviewQuestions(student, dto));
        mockInterview.setStudentAnswers(null);
        mockInterview.setFeedback(null);
        mockInterview.setScore(null);
        mockInterview.setUrl(null);
        mockInterview.setMeetingProvider(null);
        mockInterview.setExternalMeetingId(null);

        mockInterviewRepository.save(mockInterview);
    }

    public void submitAiInterviewAnswers(Integer studentId, Integer mockInterviewId, AiInterviewAnswerDTOIN dto) {

        Student student = studentRepository.findStudentById(studentId);

        if (student == null) {
            throw new ApiException("Student with id " + studentId + " not found");
        }

        MockInterview mockInterview = mockInterviewRepository.findMockInterviewById(mockInterviewId);

        if (mockInterview == null) {
            throw new ApiException("Mock interview with id " + mockInterviewId + " not found");
        }

        if (!mockInterview.getInterviewMode().equals("AI")) {
            throw new ApiException("This endpoint is only for AI interviews");
        }

        if (mockInterview.getStudent() == null || !mockInterview.getStudent().getId().equals(studentId)) {
            throw new ApiException("This AI interview does not belong to this student");
        }

        if (mockInterview.getStatus().equals("COMPLETE")) {
            throw new ApiException("This AI interview is already completed");
        }

        mockInterview.setStudentAnswers(dto.getStudentAnswers());

        String json = evaluateAiInterviewAnswers(student, mockInterview, dto.getStudentAnswers());

        mockInterview.setFeedback(parseTextField(json, FEEDBACK_PATTERN, "feedback"));
        mockInterview.setScore(parseScore(json));
        mockInterview.setStatus("COMPLETE");

        mockInterviewRepository.save(mockInterview);
    }

    public List<MockInterviewDTOOUT> getStudentAiInterviews(Integer studentId) {

        Student student = studentRepository.findStudentById(studentId);

        if (student == null) {
            throw new ApiException("Student with id " + studentId + " not found");
        }

        List<MockInterview> mockInterviews =
                mockInterviewRepository.findMockInterviewsByStudentIdAndInterviewMode(studentId, "AI");

        List<MockInterviewDTOOUT> dtoOuts = new ArrayList<>();

        for (MockInterview mockInterview : mockInterviews) {
            dtoOuts.add(toDtoOut(mockInterview));
        }

        return dtoOuts;
    }

    // =========================
    // COMMON CRUD
    // =========================

    public MockInterviewDTOOUT getById(Integer id) {

        MockInterview mockInterview = mockInterviewRepository.findMockInterviewById(id);

        if (mockInterview == null) {
            throw new ApiException("Mock interview with id " + id + " not found");
        }

        return toDtoOut(mockInterview);
    }

    public List<MockInterviewDTOOUT> getAll() {

        List<MockInterview> mockInterviews = mockInterviewRepository.findAll();

        List<MockInterviewDTOOUT> dtoOuts = new ArrayList<>();

        for (MockInterview mockInterview : mockInterviews) {
            dtoOuts.add(toDtoOut(mockInterview));
        }

        return dtoOuts;
    }

    public void delete(Integer id) {

        MockInterview mockInterview = mockInterviewRepository.findMockInterviewById(id);

        if (mockInterview == null) {
            throw new ApiException("Mock interview with id " + id + " not found");
        }

        mockInterviewRepository.delete(mockInterview);
    }

    // =========================
    // AI PROMPTS
    // =========================

    private String generateMentorQuestions(Student student, Mentor mentor, MockInterviewDTOIN dto) {

        String cv = student.getCvText() == null || student.getCvText().isBlank()
                ? "(no CV provided)"
                : student.getCvText();

        String prompt = """
                You are helping a mentor prepare for a mock interview.
                
                Respond with JSON only using this exact shape:
                {"questions": "Question 1...\\nQuestion 2...\\nQuestion 3...\\nQuestion 4...\\nQuestion 5..."}
                
                Generate 10 suggested questions. These questions are only suggestions for the mentor.
                
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
                
                Student CV:
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
                cv
        );

        String json = aiService.ask(prompt);

        return parseQuestions(json);
    }

    private String generateAiInterviewQuestions(Student student, AiMockInterviewDTOIN dto) {

        String cv = student.getCvText() == null || student.getCvText().isBlank()
                ? "(no CV provided)"
                : student.getCvText();

        String prompt = """
                You are an AI mock interviewer.
                
                Respond with JSON only using this exact shape:
                {"questions": "Question 1...\\nQuestion 2...\\nQuestion 3...\\nQuestion 4...\\nQuestion 5..."}
                
                Generate 10 interview questions for the student.
                The questions must match the interview type, target role, and student profile.
                
                Interview type: %s
                Interview description: %s
                
                Student name: %s
                Student major: %s
                Student target role: %s
                Student years of experience: %s
                Student readiness score: %s
                
                Student CV:
                %s
                """.formatted(
                dto.getInterviewType(),
                dto.getDescription(),
                student.getFullName(),
                student.getMajor(),
                student.getTargetRole(),
                student.getYearsExperience(),
                student.getReadinessScore(),
                cv
        );

        String json = aiService.ask(prompt);

        return parseQuestions(json);
    }

    private String evaluateAiInterviewAnswers(Student student, MockInterview mockInterview, String studentAnswers) {

        String prompt = """
                You are an AI interview evaluator.
                
                Respond with JSON only using this exact shape:
                {
                  "feedback": "...",
                  "score": 0
                }
                
                score must be an integer from 0 to 100.
                
                Evaluate the student's answers based on:
                - correctness
                - clarity
                - depth
                - relevance to the interview questions
                - readiness for the target role
                
                Student name: %s
                Student major: %s
                Student target role: %s
                Student readiness score: %s
                
                Interview type: %s
                Interview description: %s
                
                Questions:
                %s
                
                Student answers:
                %s
                """.formatted(
                student.getFullName(),
                student.getMajor(),
                student.getTargetRole(),
                student.getReadinessScore(),
                mockInterview.getInterviewType(),
                mockInterview.getDescription(),
                mockInterview.getQuestions(),
                studentAnswers
        );

        return aiService.ask(prompt);
    }

    // =========================
    // PARSING
    // =========================

    private String parseQuestions(String json) {

        Matcher matcher = QUESTIONS_PATTERN.matcher(json);

        if (!matcher.find()) {
            throw new AiException("AI response did not contain questions.");
        }

        String questions = matcher.group(1)
                .replace("\\n", "\n")
                .replace("\\\"", "\"");

        if (questions.isBlank()) {
            throw new AiException("AI returned empty questions.");
        }

        return questions;
    }

    private String parseTextField(String json, Pattern pattern, String fieldName) {

        Matcher matcher = pattern.matcher(json);

        if (!matcher.find()) {
            throw new AiException("AI response did not contain " + fieldName + ".");
        }

        String value = matcher.group(1)
                .replace("\\n", "\n")
                .replace("\\\"", "\"");

        if (value.isBlank()) {
            throw new AiException("AI returned empty " + fieldName + ".");
        }

        return value;
    }

    private Integer parseScore(String json) {

        Matcher matcher = SCORE_PATTERN.matcher(json);

        if (!matcher.find()) {
            throw new AiException("AI response did not contain score.");
        }

        int score = Integer.parseInt(matcher.group(1));

        if (score < 0 || score > 100) {
            throw new AiException("AI score must be between 0 and 100.");
        }

        return score;
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