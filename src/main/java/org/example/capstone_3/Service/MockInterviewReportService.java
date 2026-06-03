package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.AI.AiException;
import org.example.capstone_3.AI.AiService;
import org.example.capstone_3.Api.ApiException;
import org.example.capstone_3.DTO.OUT.MockInterviewReportDTOOUT;
import org.example.capstone_3.Model.MockInterview;
import org.example.capstone_3.Model.MockInterviewReport;
import org.example.capstone_3.Model.Student;
import org.example.capstone_3.Repository.MockInterviewReportRepository;
import org.example.capstone_3.Repository.MockInterviewRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MockInterviewReportService {

    private static final Pattern SUMMARY_PATTERN =
            Pattern.compile("\"summary\"\\s*:\\s*\"(.*?)\"", Pattern.DOTALL);

    private static final Pattern STRENGTHS_PATTERN =
            Pattern.compile("\"strengths\"\\s*:\\s*\"(.*?)\"", Pattern.DOTALL);

    private static final Pattern WEAKNESSES_PATTERN =
            Pattern.compile("\"weaknesses\"\\s*:\\s*\"(.*?)\"", Pattern.DOTALL);

    private static final Pattern RECOMMENDATIONS_PATTERN =
            Pattern.compile("\"recommendations\"\\s*:\\s*\"(.*?)\"", Pattern.DOTALL);

    private final MockInterviewReportRepository mockInterviewReportRepository;
    private final MockInterviewRepository mockInterviewRepository;
    private final AiService aiService;

    public void create(Integer mockInterviewId) {

        MockInterview mockInterview = mockInterviewRepository.findMockInterviewById(mockInterviewId);

        if (mockInterview == null) {
            throw new ApiException("Mock interview with id " + mockInterviewId + " not found");
        }

        if (!mockInterview.getStatus().equals("COMPLETE")) {
            throw new ApiException("Mock interview report can be generated only after interview is completed");
        }

        if (mockInterview.getStudent() == null) {
            throw new ApiException("Mock interview is not linked to a student");
        }

        if (mockInterviewReportRepository.findMockInterviewReportByMockInterviewId(mockInterviewId) != null) {
            throw new ApiException("This mock interview already has a report");
        }

        String json = aiService.ask(buildReportPrompt(mockInterview));

        MockInterviewReport report = new MockInterviewReport();

        report.setSummary(parseField(json, SUMMARY_PATTERN, "summary"));
        report.setStrengths(parseField(json, STRENGTHS_PATTERN, "strengths"));
        report.setWeaknesses(parseField(json, WEAKNESSES_PATTERN, "weaknesses"));
        report.setRecommendations(parseField(json, RECOMMENDATIONS_PATTERN, "recommendations"));
        report.setGeneratedAt(LocalDateTime.now());
        report.setStudent(mockInterview.getStudent());
        report.setMockInterview(mockInterview);

        mockInterviewReportRepository.save(report);
    }

    public MockInterviewReportDTOOUT getById(Integer id) {

        MockInterviewReport report = mockInterviewReportRepository.findMockInterviewReportById(id);

        if (report == null) {
            throw new ApiException("Mock interview report with id " + id + " not found");
        }

        return toDtoOut(report);
    }

    public List<MockInterviewReportDTOOUT> getAll() {

        List<MockInterviewReport> reports = mockInterviewReportRepository.findAll();

        List<MockInterviewReportDTOOUT> reportDTOOUTS = new ArrayList<>();

        for (MockInterviewReport report : reports) {
            reportDTOOUTS.add(toDtoOut(report));
        }

        return reportDTOOUTS;
    }

    public void update(Integer id) {

        MockInterviewReport report = mockInterviewReportRepository.findMockInterviewReportById(id);

        if (report == null) {
            throw new ApiException("Mock interview report with id " + id + " not found");
        }

        MockInterview mockInterview = report.getMockInterview();

        if (mockInterview == null) {
            throw new ApiException("Report is not linked to a mock interview");
        }

        if (!mockInterview.getStatus().equals("COMPLETE")) {
            throw new ApiException("Mock interview report can be regenerated only after interview is completed");
        }

        String json = aiService.ask(buildReportPrompt(mockInterview));

        report.setSummary(parseField(json, SUMMARY_PATTERN, "summary"));
        report.setStrengths(parseField(json, STRENGTHS_PATTERN, "strengths"));
        report.setWeaknesses(parseField(json, WEAKNESSES_PATTERN, "weaknesses"));
        report.setRecommendations(parseField(json, RECOMMENDATIONS_PATTERN, "recommendations"));
        report.setGeneratedAt(LocalDateTime.now());

        mockInterviewReportRepository.save(report);
    }

    public void delete(Integer id) {

        MockInterviewReport report = mockInterviewReportRepository.findMockInterviewReportById(id);

        if (report == null) {
            throw new ApiException("Mock interview report with id " + id + " not found");
        }

        mockInterviewReportRepository.delete(report);
    }

    private String buildReportPrompt(MockInterview mockInterview) {

        Student student = mockInterview.getStudent();

        String studentAnswers = mockInterview.getStudentAnswers() == null || mockInterview.getStudentAnswers().isBlank()
                ? "(no student answers provided)"
                : mockInterview.getStudentAnswers();

        String feedback = mockInterview.getFeedback() == null || mockInterview.getFeedback().isBlank()
                ? "(no mentor feedback provided)"
                : mockInterview.getFeedback();

        String questions = mockInterview.getQuestions() == null || mockInterview.getQuestions().isBlank()
                ? "(no questions provided)"
                : mockInterview.getQuestions();

        return """
                You are an interview performance evaluator.
                
                Respond with JSON only using this exact shape:
                {
                  "summary": "...",
                  "strengths": "...",
                  "weaknesses": "...",
                  "recommendations": "..."
                }
                
                Create a clear mock interview report based on the data below.
                
                Student name: %s
                Student major: %s
                Student target role: %s
                Student readiness score: %s
                
                Interview type: %s
                Interview status: %s
                
                Questions:
                %s
                
                Student answers:
                %s
                
                Mentor feedback:
                %s
                
                Score:
                %s
                """.formatted(
                student.getFullName(),
                student.getMajor(),
                student.getTargetRole(),
                student.getReadinessScore(),
                mockInterview.getInterviewType(),
                mockInterview.getStatus(),
                questions,
                studentAnswers,
                feedback,
                mockInterview.getScore()
        );
    }

    private String parseField(String json, Pattern pattern, String fieldName) {

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

    private MockInterviewReportDTOOUT toDtoOut(MockInterviewReport report) {

        Integer studentId = report.getStudent() != null ? report.getStudent().getId() : null;

        Integer mockInterviewId = report.getMockInterview() != null ? report.getMockInterview().getId() : null;

        return new MockInterviewReportDTOOUT(
                report.getId(),
                report.getSummary(),
                report.getStrengths(),
                report.getWeaknesses(),
                report.getRecommendations(),
                report.getGeneratedAt(),
                studentId,
                mockInterviewId
        );
    }
}