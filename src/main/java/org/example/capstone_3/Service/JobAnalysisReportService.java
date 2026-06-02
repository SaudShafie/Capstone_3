package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiException;
import org.example.capstone_3.DTO.IN.JobAnalysisReportDTOIn;
import org.example.capstone_3.DTO.OUT.JobAnalysisReportDTOOut;
import org.example.capstone_3.Model.JobAnalysis;
import org.example.capstone_3.Model.JobAnalysisReport;
import org.example.capstone_3.Model.Student;
import org.example.capstone_3.Repository.JobAnalysisReportRepository;
import org.example.capstone_3.Repository.JobAnalysisRepository;
import org.example.capstone_3.Repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobAnalysisReportService {

    private final JobAnalysisReportRepository jobAnalysisReportRepository;
    private final JobAnalysisRepository jobAnalysisRepository;
    private final StudentRepository studentRepository;

    public JobAnalysisReportDTOOut create(JobAnalysisReportDTOIn dto) {
        JobAnalysis jobAnalysis = findJobAnalysis(dto.getJobAnalysisId());
        Student student = findStudent(dto.getStudentId());

        if (jobAnalysis.getStudent() != null && student != null
                && !jobAnalysis.getStudent().getId().equals(student.getId())) {
            throw new ApiException("This job analysis does not belong to this student");
        }

        JobAnalysisReport report = new JobAnalysisReport();
        applyDto(report, dto);
        report.setGeneratedAt(LocalDateTime.now());
        report.setStudent(student != null ? student : jobAnalysis.getStudent());
        report.setJobAnalysis(jobAnalysis);
        return toDtoOut(jobAnalysisReportRepository.save(report));
    }

    public JobAnalysisReportDTOOut getById(Integer id) {
        JobAnalysisReport report = jobAnalysisReportRepository.findJobAnalysisReportById(id);
        if (report == null) {
            throw new ApiException("Job analysis report with id " + id + " not found");
        }
        return toDtoOut(report);
    }

    public List<JobAnalysisReportDTOOut> getAll() {
        return jobAnalysisReportRepository.findAll().stream().map(this::toDtoOut).toList();
    }

    public JobAnalysisReportDTOOut update(Integer id, JobAnalysisReportDTOIn dto) {
        JobAnalysisReport report = jobAnalysisReportRepository.findJobAnalysisReportById(id);
        if (report == null) {
            throw new ApiException("Job analysis report with id " + id + " not found");
        }
        applyDto(report, dto);
        if (dto.getStudentId() != null) {
            report.setStudent(findStudent(dto.getStudentId()));
        }
        if (dto.getJobAnalysisId() != null) {
            report.setJobAnalysis(findJobAnalysis(dto.getJobAnalysisId()));
        }
        return toDtoOut(jobAnalysisReportRepository.save(report));
    }

    public void delete(Integer id) {
        JobAnalysisReport report = jobAnalysisReportRepository.findJobAnalysisReportById(id);
        if (report == null) {
            throw new ApiException("Job analysis report with id " + id + " not found");
        }
        jobAnalysisReportRepository.deleteById(id);
    }

    private void applyDto(JobAnalysisReport report, JobAnalysisReportDTOIn dto) {
        report.setSummary(dto.getSummary());
        report.setImprovements(dto.getImprovements());
        report.setRecommendations(dto.getRecommendations());
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

    private JobAnalysis findJobAnalysis(Integer jobAnalysisId) {
        if (jobAnalysisId == null) {
            throw new ApiException("Job analysis id is required");
        }
        JobAnalysis jobAnalysis = jobAnalysisRepository.findJobAnalysisById(jobAnalysisId);
        if (jobAnalysis == null) {
            throw new ApiException("Job analysis with id " + jobAnalysisId + " not found");
        }
        return jobAnalysis;
    }

    private JobAnalysisReportDTOOut toDtoOut(JobAnalysisReport report) {
        Integer studentId = report.getStudent() != null ? report.getStudent().getId() : null;
        String studentName = report.getStudent() != null ? report.getStudent().getFullName() : null;
        Integer jobAnalysisId = report.getJobAnalysis() != null ? report.getJobAnalysis().getId() : null;
        String jobTitle = report.getJobAnalysis() != null ? report.getJobAnalysis().getJobTitle() : null;
        return new JobAnalysisReportDTOOut(
                report.getId(),
                report.getSummary(),
                report.getImprovements(),
                report.getRecommendations(),
                report.getGeneratedAt(),
                studentId,
                studentName,
                jobAnalysisId,
                jobTitle
        );
    }
}
