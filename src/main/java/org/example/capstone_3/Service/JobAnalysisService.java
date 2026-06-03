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
import org.example.capstone_3.Repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class JobAnalysisService {

    private final JobAnalysisRepository jobAnalysisRepository;
    private final StudentRepository studentRepository;

    public void create(Integer studentId, JobAnalysisDTOIn dto) {

        Student student = studentRepository.findStudentById(studentId);

        if (student == null) {
            throw new ApiException("Student with id " + studentId + " not found");
        }

        JobAnalysis jobAnalysis = new JobAnalysis();

        applyDto(jobAnalysis, dto);

        jobAnalysis.setStudent(student);
        jobAnalysis.setCreatedAt(LocalDateTime.now());

        // مؤقتًا إلى أن تربطين AI
        jobAnalysis.setJobTitle("Not generated yet");
        jobAnalysis.setRequiredSkillsText("");
        jobAnalysis.setMissingSkillsText("");
        jobAnalysis.setMatchScore(0);
        jobAnalysis.setRecommendations("");
        jobAnalysis.setSkills(null);

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

        List<JobAnalysis> jobAnalyses = jobAnalysisRepository.findAll();

        List<JobAnalysisDTOOut> jobAnalysisDTOOuts = new ArrayList<>();

        for (JobAnalysis jobAnalysis : jobAnalyses) {
            jobAnalysisDTOOuts.add(toDtoOut(jobAnalysis));
        }

        return jobAnalysisDTOOuts;
    }

    public List<JobAnalysisDTOOut> getByStudentId(Integer studentId) {

        Student student = studentRepository.findStudentById(studentId);

        if (student == null) {
            throw new ApiException("Student with id " + studentId + " not found");
        }

        List<JobAnalysis> jobAnalyses = jobAnalysisRepository.findJobAnalysesByStudentId(studentId);

        List<JobAnalysisDTOOut> jobAnalysisDTOOuts = new ArrayList<>();

        for (JobAnalysis jobAnalysis : jobAnalyses) {
            jobAnalysisDTOOuts.add(toDtoOut(jobAnalysis));
        }

        return jobAnalysisDTOOuts;
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

        jobAnalysisRepository.delete(jobAnalysis);
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
                mapStudent(jobAnalysis.getStudent()),
                mapSkills(jobAnalysis.getSkills())
        );
    }

    private StudentSummaryDTOOut mapStudent(Student student) {

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

    private Set<SkillDTOOut> mapSkills(Set<Skill> skills) {

        Set<SkillDTOOut> skillDTOOuts = new HashSet<>();

        if (skills == null) {
            return skillDTOOuts;
        }

        for (Skill skill : skills) {
            SkillDTOOut skillDTOOut = new SkillDTOOut(
                    skill.getId(),
                    skill.getName(),
                    skill.getCategory()
            );

            skillDTOOuts.add(skillDTOOut);
        }

        return skillDTOOuts;
    }
}