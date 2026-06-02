package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class MockInterviewService {

    private final MockInterviewRepository mockInterviewRepository;
    private final StudentRepository studentRepository;
    private final MentorRepository mentorRepository;
    private final JobAnalysisRepository jobAnalysisRepository;

    public MockInterviewDTOOUT create(MockInterviewDTOIN dto) {
        MockInterview mockInterview = new MockInterview();
        applyDto(mockInterview, dto);
        return toDtoOut(mockInterviewRepository.save(mockInterview));
    }

    public MockInterviewDTOOUT getById(Integer id) {
        MockInterview mockInterview = mockInterviewRepository.findMockInterviewById(id);
        if (mockInterview == null) {
            throw new ApiException("Mock interview with id " + id + " not found");
        }
        return toDtoOut(mockInterview);
    }

    public List<MockInterviewDTOOUT> getAll() {
        return mockInterviewRepository.findAll().stream().map(this::toDtoOut).toList();
    }

    public MockInterviewDTOOUT update(Integer id, MockInterviewDTOIN dto) {
        MockInterview mockInterview = mockInterviewRepository.findMockInterviewById(id);
        if (mockInterview == null) {
            throw new ApiException("Mock interview with id " + id + " not found");
        }
        applyDto(mockInterview, dto);
        return toDtoOut(mockInterviewRepository.save(mockInterview));
    }

    public void delete(Integer id) {
        MockInterview mockInterview = mockInterviewRepository.findMockInterviewById(id);
        if (mockInterview == null) {
            throw new ApiException("Mock interview with id " + id + " not found");
        }
        mockInterviewRepository.deleteById(id);
    }

    private void applyDto(MockInterview mockInterview, MockInterviewDTOIN dto) {
        mockInterview.setInterviewType(dto.getInterviewType());
        mockInterview.setScheduledAt(dto.getScheduledAt());
        mockInterview.setStatus(dto.getStatus());
        mockInterview.setQuestions(dto.getQuestions());
        mockInterview.setStudentAnswers(dto.getStudentAnswers());
        mockInterview.setFeedback(dto.getFeedback());
        mockInterview.setScore(dto.getScore());
        mockInterview.setUrl(dto.getUrl());
        mockInterview.setCreatedAt(dto.getCreatedAt());
        mockInterview.setStudent(findStudent(dto.getStudentId()));
        mockInterview.setMentor(findMentor(dto.getMentorId()));
        mockInterview.setJobAnalysis(findJobAnalysis(dto.getJobAnalysisId()));
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

    private Mentor findMentor(Integer mentorId) {
        if (mentorId == null) {
            return null;
        }
        Mentor mentor = mentorRepository.findMentorById(mentorId);
        if (mentor == null) {
            throw new ApiException("Mentor with id " + mentorId + " not found");
        }
        return mentor;
    }

    private JobAnalysis findJobAnalysis(Integer jobAnalysisId) {
        if (jobAnalysisId == null) {
            return null;
        }
        JobAnalysis jobAnalysis = jobAnalysisRepository.findJobAnalysisById(jobAnalysisId);
        if (jobAnalysis == null) {
            throw new ApiException("Job analysis with id " + jobAnalysisId + " not found");
        }
        return jobAnalysis;
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
