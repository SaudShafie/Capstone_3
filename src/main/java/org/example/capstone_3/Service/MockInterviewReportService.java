package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiException;
import org.example.capstone_3.DTO.IN.MockInterviewReportDTOIN;
import org.example.capstone_3.DTO.OUT.MockInterviewReportDTOOUT;
import org.example.capstone_3.Model.MockInterviewReport;
import org.example.capstone_3.Repository.MockInterviewReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MockInterviewReportService {

    private final MockInterviewReportRepository mockInterviewReportRepository;

    public MockInterviewReportDTOOUT create(MockInterviewReportDTOIN dto) {
        MockInterviewReport mockInterviewReport = new MockInterviewReport();
        return toDtoOut(mockInterviewReportRepository.save(mockInterviewReport));
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

    public MockInterviewReportDTOOUT update(Integer id, MockInterviewReportDTOIN dto) {
        MockInterviewReport mockInterviewReport = mockInterviewReportRepository.findMockInterviewReportById(id);
        if (mockInterviewReport == null) {
            throw new ApiException("Mock interview report with id " + id + " not found");
        }
        return toDtoOut(mockInterviewReportRepository.save(mockInterviewReport));
    }

    public void delete(Integer id) {
        MockInterviewReport mockInterviewReport = mockInterviewReportRepository.findMockInterviewReportById(id);
        if (mockInterviewReport == null) {
            throw new ApiException("Mock interview report with id " + id + " not found");
        }
        mockInterviewReportRepository.deleteById(id);
    }

    private MockInterviewReportDTOOUT toDtoOut(MockInterviewReport mockInterviewReport) {
        return new MockInterviewReportDTOOUT(mockInterviewReport.getId());
    }
}
