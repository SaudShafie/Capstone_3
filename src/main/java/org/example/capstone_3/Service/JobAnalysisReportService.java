package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiException;
import org.example.capstone_3.DTO.IN.JobAnalysisReportDTOIn;
import org.example.capstone_3.DTO.OUT.JobAnalysisReportDTOOut;
import org.example.capstone_3.DTO.OUT.JobAnalysisSummaryDTOOut;
import org.example.capstone_3.DTO.OUT.StudentSummaryDTOOut;
import org.example.capstone_3.Model.JobAnalysis;
import org.example.capstone_3.Model.JobAnalysisReport;
import org.example.capstone_3.Model.Student;
import org.example.capstone_3.Repository.JobAnalysisReportRepository;
import org.example.capstone_3.Repository.JobAnalysisRepository;
import org.example.capstone_3.Repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobAnalysisReportService {

    private final JobAnalysisReportRepository jobAnalysisReportRepository;
    private final JobAnalysisRepository jobAnalysisRepository;
    private final StudentRepository studentRepository;

    public void create(Integer jobAnalysisId) {

        JobAnalysis jobAnalysis = jobAnalysisRepository.findJobAnalysisById(jobAnalysisId);

        if (jobAnalysis == null) {
            throw new ApiException("Job analysis with id " + jobAnalysisId + " not found");
        }

        if (jobAnalysis.getStudent() == null) {
            throw new ApiException("Job analysis is not linked to any student");
        }

        if (jobAnalysisReportRepository.findJobAnalysisReportByJobAnalysisId(jobAnalysisId) != null) {
            throw new ApiException("This job analysis already has a report");
        }

        JobAnalysisReport report = new JobAnalysisReport();

        report.setJobAnalysis(jobAnalysis);
        report.setStudent(jobAnalysis.getStudent());
        report.setGeneratedAt(LocalDateTime.now());

        // مؤقتًا إلى أن تربطين AI
        report.setSummary("Report summary has not been generated yet");
        report.setImprovements("Improvements have not been generated yet");
        report.setRecommendations("Recommendations have not been generated yet");

        jobAnalysisReportRepository.save(report);
    }

    public JobAnalysisReportDTOOut getById(Integer id) {

        JobAnalysisReport report = jobAnalysisReportRepository.findJobAnalysisReportById(id);

        if (report == null) {
            throw new ApiException("Job analysis report with id " + id + " not found");
        }

        return toDtoOut(report);
    }

    public List<JobAnalysisReportDTOOut> getAll() {

        List<JobAnalysisReport> reports = jobAnalysisReportRepository.findAll();

        List<JobAnalysisReportDTOOut> reportDTOOuts = new ArrayList<>();

        for (JobAnalysisReport report : reports) {
            reportDTOOuts.add(toDtoOut(report));
        }

        return reportDTOOuts;
    }

    public List<JobAnalysisReportDTOOut> getByStudentId(Integer studentId) {

        Student student = studentRepository.findStudentById(studentId);

        if (student == null) {
            throw new ApiException("Student with id " + studentId + " not found");
        }

        List<JobAnalysisReport> reports = jobAnalysisReportRepository.findJobAnalysisReportsByStudentId(studentId);

        List<JobAnalysisReportDTOOut> reportDTOOuts = new ArrayList<>();

        for (JobAnalysisReport report : reports) {
            reportDTOOuts.add(toDtoOut(report));
        }

        return reportDTOOuts;
    }

    public void update(Integer id) {

        JobAnalysisReport report = jobAnalysisReportRepository.findJobAnalysisReportById(id);

        if (report == null) {
            throw new ApiException("Job analysis report with id " + id + " not found");
        }

        // مؤقتًا: لاحقًا هنا يصير regenerate من AI
        report.setGeneratedAt(LocalDateTime.now());
        report.setSummary("Report summary has been regenerated");
        report.setImprovements("Improvements have been regenerated");
        report.setRecommendations("Recommendations have been regenerated");

        jobAnalysisReportRepository.save(report);
    }

    public void delete(Integer id) {

        JobAnalysisReport report = jobAnalysisReportRepository.findJobAnalysisReportById(id);

        if (report == null) {
            throw new ApiException("Job analysis report with id " + id + " not found");
        }

        jobAnalysisReportRepository.delete(report);
    }

    private JobAnalysisReportDTOOut toDtoOut(JobAnalysisReport report) {
        return new JobAnalysisReportDTOOut(
                report.getId(),
                report.getSummary(),
                report.getImprovements(),
                report.getRecommendations(),
                mapStudent(report.getStudent()),
                mapJobAnalysis(report.getJobAnalysis())
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

    private JobAnalysisSummaryDTOOut mapJobAnalysis(JobAnalysis jobAnalysis) {

        if (jobAnalysis == null) {
            return null;
        }

        return new JobAnalysisSummaryDTOOut(
                jobAnalysis.getId(),
                jobAnalysis.getJobTitle(),
                jobAnalysis.getMatchScore(),
                jobAnalysis.getMissingSkillsText()
        );
    }
}