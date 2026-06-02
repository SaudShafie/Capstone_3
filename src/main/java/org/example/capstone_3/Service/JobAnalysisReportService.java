package org.example.capstone_3.Service;

import org.example.capstone_3.DTO.IN.JobAnalysisReportDTOIn;
import org.example.capstone_3.DTO.OUT.JobAnalysisReportDTOOut;
import org.example.capstone_3.Model.JobAnalysis;
import org.example.capstone_3.Model.JobAnalysisReport;
import org.example.capstone_3.Model.Student;
import org.example.capstone_3.Repository.JobAnalysisReportRepository;
import org.example.capstone_3.Repository.JobAnalysisRepository;
import org.example.capstone_3.Repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobAnalysisReportService {

    private final JobAnalysisReportRepository jobAnalysisReportRepository;
    private final StudentRepository studentRepository;
    private final JobAnalysisRepository jobAnalysisRepository;

    public void addJobAnalysisReport(Integer studentId,
                                     Integer jobAnalysisId,
                                     JobAnalysisReportDTOIn jobAnalysisReportDTOIn) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        JobAnalysis jobAnalysis = jobAnalysisRepository.findById(jobAnalysisId)
                .orElseThrow(() -> new RuntimeException("Job analysis not found"));

        if (!jobAnalysis.getStudent().getId().equals(studentId)) {
            throw new RuntimeException("This job analysis does not belong to this student");
        }

        JobAnalysisReport existingReport =
                jobAnalysisReportRepository.findJobAnalysisReportByJobAnalysisId(jobAnalysisId);

        if (existingReport != null) {
            throw new RuntimeException("This job analysis already has a report");
        }

        JobAnalysisReport report = new JobAnalysisReport();

        report.setSummary(jobAnalysisReportDTOIn.getSummary());
        report.setImprovements(jobAnalysisReportDTOIn.getImprovements());
        report.setRecommendations(jobAnalysisReportDTOIn.getRecommendations());
        report.setGeneratedAt(LocalDateTime.now());

        report.setStudent(student);
        report.setJobAnalysis(jobAnalysis);

        jobAnalysisReportRepository.save(report);
    }

    public List<JobAnalysisReportDTOOut> getAllJobAnalysisReports() {

        List<JobAnalysisReport> reports = jobAnalysisReportRepository.findAll();

        List<JobAnalysisReportDTOOut> reportDTOOuts = new ArrayList<>();

        for (JobAnalysisReport report : reports) {
            reportDTOOuts.add(mapToJobAnalysisReportDTOOut(report));
        }

        return reportDTOOuts;
    }

    public JobAnalysisReportDTOOut getJobAnalysisReportById(Integer reportId) {

        JobAnalysisReport report = jobAnalysisReportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Job analysis report not found"));

        return mapToJobAnalysisReportDTOOut(report);
    }

    public List<JobAnalysisReportDTOOut> getReportsByStudentId(Integer studentId) {

        studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<JobAnalysisReport> reports =
                jobAnalysisReportRepository.findJobAnalysisReportsByStudentId(studentId);

        List<JobAnalysisReportDTOOut> reportDTOOuts = new ArrayList<>();

        for (JobAnalysisReport report : reports) {
            reportDTOOuts.add(mapToJobAnalysisReportDTOOut(report));
        }

        return reportDTOOuts;
    }

    public JobAnalysisReportDTOOut getReportByJobAnalysisId(Integer jobAnalysisId) {

        jobAnalysisRepository.findById(jobAnalysisId)
                .orElseThrow(() -> new RuntimeException("Job analysis not found"));

        JobAnalysisReport report =
                jobAnalysisReportRepository.findJobAnalysisReportByJobAnalysisId(jobAnalysisId);

        if (report == null) {
            throw new RuntimeException("Report not found for this job analysis");
        }

        return mapToJobAnalysisReportDTOOut(report);
    }

    public void updateJobAnalysisReport(Integer reportId,
                                        JobAnalysisReportDTOIn jobAnalysisReportDTOIn) {

        JobAnalysisReport report = jobAnalysisReportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Job analysis report not found"));

        report.setSummary(jobAnalysisReportDTOIn.getSummary());
        report.setImprovements(jobAnalysisReportDTOIn.getImprovements());
        report.setRecommendations(jobAnalysisReportDTOIn.getRecommendations());

        jobAnalysisReportRepository.save(report);
    }

    public void deleteJobAnalysisReport(Integer reportId) {

        JobAnalysisReport report = jobAnalysisReportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Job analysis report not found"));

        jobAnalysisReportRepository.delete(report);
    }

    private JobAnalysisReportDTOOut mapToJobAnalysisReportDTOOut(JobAnalysisReport report) {

        return new JobAnalysisReportDTOOut(
                report.getId(),
                report.getSummary(),
                report.getImprovements(),
                report.getRecommendations(),
                report.getGeneratedAt(),
                report.getStudent().getId(),
                report.getStudent().getFullName(),
                report.getJobAnalysis().getId(),
                report.getJobAnalysis().getJobTitle()
        );
    }
}