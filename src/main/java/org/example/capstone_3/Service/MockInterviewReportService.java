package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiException;
import org.example.capstone_3.DTO.IN.MockInterviewReportDTOIN;
import org.example.capstone_3.DTO.OUT.MockInterviewReportDTOOUT;
import org.example.capstone_3.Model.MockInterview;
import org.example.capstone_3.Model.MockInterviewReport;
import org.example.capstone_3.Model.Student;
import org.example.capstone_3.Repository.MockInterviewReportRepository;
import org.example.capstone_3.Repository.MockInterviewRepository;
import org.example.capstone_3.Repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MockInterviewReportService {

    private final MockInterviewReportRepository mockInterviewReportRepository;
    private final MockInterviewRepository mockInterviewRepository;
    private final StudentRepository studentRepository;

    public void create(MockInterviewReportDTOIN dto) {
        MockInterviewReport mockInterviewReport = new MockInterviewReport();
        applyDto(mockInterviewReport, dto);
        mockInterviewReportRepository.save(mockInterviewReport);
    }

    public MockInterviewReportDTOOUT getById(Integer id) {
        MockInterviewReport mockInterviewReport = mockInterviewReportRepository.findMockInterviewReportById(id);
        if (mockInterviewReport == null) {
            throw new ApiException("Mock interview report with id " + id + " not found");
        }
        return toDtoOut(mockInterviewReport);
    }

    public List<MockInterviewReportDTOOUT> getAll() {
        return mockInterviewReportRepository.findAll().stream().map(this::toDtoOut).toList();
    }

    public void update(Integer id, MockInterviewReportDTOIN dto) {
        MockInterviewReport mockInterviewReport = mockInterviewReportRepository.findMockInterviewReportById(id);
        if (mockInterviewReport == null) {
            throw new ApiException("Mock interview report with id " + id + " not found");
        }
        applyDto(mockInterviewReport, dto);
       mockInterviewReportRepository.save(mockInterviewReport);
    }

    public void delete(Integer id) {
        MockInterviewReport mockInterviewReport = mockInterviewReportRepository.findMockInterviewReportById(id);
        if (mockInterviewReport == null) {
            throw new ApiException("Mock interview report with id " + id + " not found");
        }
        mockInterviewReportRepository.deleteById(id);
    }

    private void applyDto(MockInterviewReport mockInterviewReport, MockInterviewReportDTOIN dto) {
        mockInterviewReport.setSummary(dto.getSummary());
        mockInterviewReport.setStrengths(dto.getStrengths());
        mockInterviewReport.setWeaknesses(dto.getWeaknesses());
        mockInterviewReport.setRecommendations(dto.getRecommendations());
        mockInterviewReport.setGeneratedAt(dto.getGeneratedAt());
        mockInterviewReport.setStudent(findStudent(dto.getStudentId()));
        mockInterviewReport.setMockInterview(findMockInterview(dto.getMockInterviewId()));
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

    private MockInterview findMockInterview(Integer mockInterviewId) {
        if (mockInterviewId == null) {
            return null;
        }
        MockInterview mockInterview = mockInterviewRepository.findMockInterviewById(mockInterviewId);
        if (mockInterview == null) {
            throw new ApiException("Mock interview with id " + mockInterviewId + " not found");
        }
        return mockInterview;
    }

    private MockInterviewReportDTOOUT toDtoOut(MockInterviewReport mockInterviewReport) {
        Integer studentId = mockInterviewReport.getStudent() != null ? mockInterviewReport.getStudent().getId() : null;
        Integer mockInterviewId = mockInterviewReport.getMockInterview() != null
                ? mockInterviewReport.getMockInterview().getId() : null;
        return new MockInterviewReportDTOOUT(
                mockInterviewReport.getId(),
                mockInterviewReport.getSummary(),
                mockInterviewReport.getStrengths(),
                mockInterviewReport.getWeaknesses(),
                mockInterviewReport.getRecommendations(),
                mockInterviewReport.getGeneratedAt(),
                studentId,
                mockInterviewId
        );
    }
}
