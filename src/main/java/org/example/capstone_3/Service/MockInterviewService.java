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

    public MockInterviewDTOOUT create(MockInterviewDTOIN dto) {
        MockInterview mockInterview = new MockInterview();
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
        return toDtoOut(mockInterviewRepository.save(mockInterview));
    }

    public void delete(Integer id) {
        MockInterview mockInterview = mockInterviewRepository.findMockInterviewById(id);
        if (mockInterview == null) {
            throw new ApiException("Mock interview with id " + id + " not found");
        }
        mockInterviewRepository.deleteById(id);
    }

    private MockInterviewDTOOUT toDtoOut(MockInterview mockInterview) {
        return new MockInterviewDTOOUT(mockInterview.getId());
    }
}
