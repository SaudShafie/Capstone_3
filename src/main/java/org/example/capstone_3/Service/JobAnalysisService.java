package org.example.capstone_3.Service;

import org.example.capstone_3.DTO.IN.JobAnalysisDTOIn;
import org.example.capstone_3.DTO.OUT.JobAnalysisDTOOut;
import org.example.capstone_3.Model.JobAnalysis;
import org.example.capstone_3.Model.Student;
import org.example.capstone_3.Repository.JobAnalysisRepository;
import org.example.capstone_3.Repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobAnalysisService {

    private final JobAnalysisRepository jobAnalysisRepository;
    private final StudentRepository studentRepository;

    public void addJobAnalysis(Integer studentId, JobAnalysisDTOIn jobAnalysisDTOIn) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        JobAnalysis jobAnalysis = new JobAnalysis();

        jobAnalysis.setJobTitle(jobAnalysisDTOIn.getJobTitle());
        jobAnalysis.setJobDescription(jobAnalysisDTOIn.getJobDescription());
        jobAnalysis.setRequiredSkillsText(jobAnalysisDTOIn.getRequiredSkillsText());
//        jobAnalysis.setMissingSkillsText(jobAnalysisDTOIn.getMissingSkillsText());
//        jobAnalysis.setMatchScore(jobAnalysisDTOIn.getMatchScore());
//        jobAnalysis.setRecommendations(jobAnalysisDTOIn.getRecommendations());
        jobAnalysis.setCreatedAt(LocalDateTime.now());
        jobAnalysis.setStudent(student);

        jobAnalysisRepository.save(jobAnalysis);
    }

    public List<JobAnalysisDTOOut> getAllJobAnalyses() {

        List<JobAnalysis> jobAnalyses = jobAnalysisRepository.findAll();

        List<JobAnalysisDTOOut> jobAnalysisDTOOuts = new ArrayList<>();

        for (JobAnalysis jobAnalysis : jobAnalyses) {
            jobAnalysisDTOOuts.add(mapToJobAnalysisDTOOut(jobAnalysis));
        }

        return jobAnalysisDTOOuts;
    }

    public JobAnalysisDTOOut getJobAnalysisById(Integer jobAnalysisId) {

        JobAnalysis jobAnalysis = jobAnalysisRepository.findById(jobAnalysisId)
                .orElseThrow(() -> new RuntimeException("Job analysis not found"));

        return mapToJobAnalysisDTOOut(jobAnalysis);
    }

    public List<JobAnalysisDTOOut> getJobAnalysesByStudentId(Integer studentId) {

        studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<JobAnalysis> jobAnalyses = jobAnalysisRepository.findJobAnalysesByStudentId(studentId);

        List<JobAnalysisDTOOut> jobAnalysisDTOOuts = new ArrayList<>();

        for (JobAnalysis jobAnalysis : jobAnalyses) {
            jobAnalysisDTOOuts.add(mapToJobAnalysisDTOOut(jobAnalysis));
        }

        return jobAnalysisDTOOuts;
    }

    public void updateJobAnalysis(Integer jobAnalysisId, JobAnalysisDTOIn jobAnalysisDTOIn) {

        JobAnalysis jobAnalysis = jobAnalysisRepository.findById(jobAnalysisId)
                .orElseThrow(() -> new RuntimeException("Job analysis not found"));

        jobAnalysis.setJobTitle(jobAnalysisDTOIn.getJobTitle());
        jobAnalysis.setJobDescription(jobAnalysisDTOIn.getJobDescription());
        jobAnalysis.setRequiredSkillsText(jobAnalysisDTOIn.getRequiredSkillsText());
//        jobAnalysis.setMissingSkillsText(jobAnalysisDTOIn.getMissingSkillsText());
//        jobAnalysis.setMatchScore(jobAnalysisDTOIn.getMatchScore());
//        jobAnalysis.setRecommendations(jobAnalysisDTOIn.getRecommendations());

        jobAnalysisRepository.save(jobAnalysis);
    }

    public void deleteJobAnalysis(Integer jobAnalysisId) {

        JobAnalysis jobAnalysis = jobAnalysisRepository.findById(jobAnalysisId)
                .orElseThrow(() -> new RuntimeException("Job analysis not found"));

        jobAnalysisRepository.delete(jobAnalysis);
    }

    private JobAnalysisDTOOut mapToJobAnalysisDTOOut(JobAnalysis jobAnalysis) {

        return new JobAnalysisDTOOut(
                jobAnalysis.getId(),
                jobAnalysis.getJobTitle(),
                jobAnalysis.getJobDescription(),
                jobAnalysis.getRequiredSkillsText(),
                jobAnalysis.getMissingSkillsText(),
                jobAnalysis.getMatchScore(),
                jobAnalysis.getRecommendations(),
                jobAnalysis.getStudent().getId(),
                jobAnalysis.getStudent().getFullName()
        );
    }
}