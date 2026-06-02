package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiException;
import org.example.capstone_3.DTO.IN.JobAnalysisDTOIn;
import org.example.capstone_3.DTO.OUT.JobAnalysisDTOOut;
import org.example.capstone_3.Model.JobAnalysis;
import org.example.capstone_3.Model.Student;
import org.example.capstone_3.Repository.JobAnalysisRepository;
import org.example.capstone_3.Repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobAnalysisService {

    private final JobAnalysisRepository jobAnalysisRepository;
    private final StudentRepository studentRepository;

    public JobAnalysisDTOOut create(JobAnalysisDTOIn dto) {
        JobAnalysis jobAnalysis = new JobAnalysis();
        applyDto(jobAnalysis, dto);
        jobAnalysis.setCreatedAt(LocalDateTime.now());
        return toDtoOut(jobAnalysisRepository.save(jobAnalysis));
    }

    public JobAnalysisDTOOut getById(Integer id) {
        JobAnalysis jobAnalysis = jobAnalysisRepository.findJobAnalysisById(id);
        if (jobAnalysis == null) {
            throw new ApiException("Job analysis with id " + id + " not found");
        }
        return toDtoOut(jobAnalysis);
    }

    public List<JobAnalysisDTOOut> getAll() {
        return jobAnalysisRepository.findAll().stream().map(this::toDtoOut).toList();
    }

    public JobAnalysisDTOOut update(Integer id, JobAnalysisDTOIn dto) {
        JobAnalysis jobAnalysis = jobAnalysisRepository.findJobAnalysisById(id);
        if (jobAnalysis == null) {
            throw new ApiException("Job analysis with id " + id + " not found");
        }
        applyDto(jobAnalysis, dto);
        return toDtoOut(jobAnalysisRepository.save(jobAnalysis));
    }

    public void delete(Integer id) {
        JobAnalysis jobAnalysis = jobAnalysisRepository.findJobAnalysisById(id);
        if (jobAnalysis == null) {
            throw new ApiException("Job analysis with id " + id + " not found");
        }
        jobAnalysisRepository.deleteById(id);
    }

    private void applyDto(JobAnalysis jobAnalysis, JobAnalysisDTOIn dto) {
        jobAnalysis.setJobTitle(dto.getJobTitle());
        jobAnalysis.setJobDescription(dto.getJobDescription());
        jobAnalysis.setRequiredSkillsText(dto.getRequiredSkillsText());
        jobAnalysis.setStudent(findStudent(dto.getStudentId()));
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

    private JobAnalysisDTOOut toDtoOut(JobAnalysis jobAnalysis) {
        Integer studentId = jobAnalysis.getStudent() != null ? jobAnalysis.getStudent().getId() : null;
        String studentName = jobAnalysis.getStudent() != null ? jobAnalysis.getStudent().getFullName() : null;
        return new JobAnalysisDTOOut(
                jobAnalysis.getId(),
                jobAnalysis.getJobTitle(),
                jobAnalysis.getJobDescription(),
                jobAnalysis.getRequiredSkillsText(),
                jobAnalysis.getMissingSkillsText(),
                jobAnalysis.getMatchScore(),
                jobAnalysis.getRecommendations(),
                studentId,
                studentName
        );
    }
}
