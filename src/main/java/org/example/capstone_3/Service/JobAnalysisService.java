package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiException;
import org.example.capstone_3.DTO.IN.JobAnalysisDTOIn;
import org.example.capstone_3.DTO.OUT.JobAnalysisDTOOut;
import org.example.capstone_3.DTO.OUT.SkillDTOOut;
import org.example.capstone_3.DTO.OUT.StudentSummaryDTOOut;
import org.example.capstone_3.Model.JobAnalysis;
import org.example.capstone_3.Model.Skill;
import org.example.capstone_3.Model.Student;
import org.example.capstone_3.Repository.JobAnalysisRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobAnalysisService {

    private final JobAnalysisRepository jobAnalysisRepository;

    public void create(JobAnalysisDTOIn dto) {
        JobAnalysis jobAnalysis = new JobAnalysis();
        applyDto(jobAnalysis, dto);
        jobAnalysis.setMatchScore(0);
        jobAnalysis.setCreatedAt(LocalDateTime.now());
        jobAnalysisRepository.save(jobAnalysis);
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

    public void update(Integer id, JobAnalysisDTOIn dto) {
        JobAnalysis jobAnalysis = jobAnalysisRepository.findJobAnalysisById(id);
        if (jobAnalysis == null) {
            throw new ApiException("Job analysis with id " + id + " not found");
        }
        applyDto(jobAnalysis, dto);
        jobAnalysisRepository.save(jobAnalysis);
    }

    public void delete(Integer id) {
        JobAnalysis jobAnalysis = jobAnalysisRepository.findJobAnalysisById(id);
        if (jobAnalysis == null) {
            throw new ApiException("Job analysis with id " + id + " not found");
        }
        jobAnalysisRepository.deleteById(id);
    }

    private void applyDto(JobAnalysis jobAnalysis, JobAnalysisDTOIn dto) {
        jobAnalysis.setJobDescription(dto.getJobDescription());
    }

    private JobAnalysisDTOOut toDtoOut(JobAnalysis jobAnalysis) {
        return new JobAnalysisDTOOut(
                jobAnalysis.getId(),
                jobAnalysis.getJobTitle(),
                jobAnalysis.getJobDescription(),
                jobAnalysis.getRequiredSkillsText(),
                jobAnalysis.getMissingSkillsText(),
                jobAnalysis.getMatchScore(),
                jobAnalysis.getRecommendations(),
                toStudentSummary(jobAnalysis.getStudent()),
                toSkillDtos(jobAnalysis.getSkills())
        );
    }

    private StudentSummaryDTOOut toStudentSummary(Student student) {
        if (student == null) {
            return null;
        }
        return new StudentSummaryDTOOut(
                student.getId(),
                student.getFullName(),
                student.getEmail(),
                student.getMajor(),
                student.getTargetRole(),
                student.getReadinessScore()
        );
    }

    private Set<SkillDTOOut> toSkillDtos(Set<Skill> skills) {
        if (skills == null) {
            return null;
        }
        return skills.stream()
                .map(skill -> new SkillDTOOut(skill.getId(), skill.getName(), skill.getCategory()))
                .collect(Collectors.toSet());
    }
}
