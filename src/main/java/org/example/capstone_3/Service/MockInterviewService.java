package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiException;
import org.example.capstone_3.DTO.IN.MockInterviewDTOIN;
import org.example.capstone_3.DTO.OUT.MockInterviewDTOOUT;
import org.example.capstone_3.Model.MockInterview;
import org.example.capstone_3.Repository.MockInterviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MockInterviewService {

    private final MockInterviewRepository mockInterviewRepository;

    public void create(MockInterviewDTOIN dto) {
        MockInterview mockInterview = new MockInterview();
        applyDto(mockInterview, dto);
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
        return mockInterviewRepository.findAll().stream().map(this::toDtoOut).toList();
    }

    public void update(Integer id, MockInterviewDTOIN dto) {
        MockInterview mockInterview = mockInterviewRepository.findMockInterviewById(id);
        if (mockInterview == null) {
            throw new ApiException("Mock interview with id " + id + " not found");
        }
        applyDto(mockInterview, dto);
        mockInterviewRepository.save(mockInterview);
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
        mockInterview.setUrl(dto.getUrl());
        mockInterview.setCreatedAt(dto.getCreatedAt());
    }

    private MockInterviewDTOOUT toDtoOut(MockInterview mockInterview) {
        Integer studentId = mockInterview.getStudent() != null ? mockInterview.getStudent().getId() : null;
        Integer mentorId = mockInterview.getMentor() != null ? mockInterview.getMentor().getId() : null;
        return new MockInterviewDTOOUT(
                mockInterview.getId(),
                mockInterview.getInterviewType(),
                mockInterview.getScheduledAt(),
                mockInterview.getStatus(),
                mockInterview.getQuestions(),
                mockInterview.getStudentAnswers(),
                mockInterview.getFeedback(),
                null,
                mockInterview.getUrl(),
                mockInterview.getCreatedAt(),
                studentId,
                mentorId,
                null
        );
    }
}
