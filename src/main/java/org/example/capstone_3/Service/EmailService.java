package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Model.Mentor;
import org.example.capstone_3.Model.MockInterviewReport;
import org.example.capstone_3.Model.MockInterview;
import org.example.capstone_3.Model.Student;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendMentorInterviewScheduledEmail(Student student, Mentor mentor, MockInterview mockInterview) {

        String subject = "CareerFit Mock Interview Scheduled";

        String body = """
                Hello,

                Your mock interview has been scheduled successfully.

                Interview Details:
                Student: %s
                Mentor: %s
                Interview Type: %s
                Date & Time: %s
                Duration: %s minutes
                Meeting Provider: %s
                Meeting Link: %s

                Please join the meeting using the link above at the scheduled time.

                CareerFit Community
                """.formatted(
                student.getFullName(),
                mentor.getFullName(),
                mockInterview.getInterviewType(),
                mockInterview.getScheduledAt(),
                mockInterview.getDurationMinutes(),
                mockInterview.getMeetingProvider(),
                mockInterview.getUrl()
        );

        sendEmail(student.getEmail(), subject, body);
        sendEmail(mentor.getEmail(), subject, body);
    }

    private void sendEmail(String to, String subject, String body) {

        if (to == null || to.isBlank()) {
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    public void sendMockInterviewReportEmail(Student student, Mentor mentor, MockInterview mockInterview, MockInterviewReport report) {

        String subject = "CareerFit Mock Interview Report";

        String body = """
            Hello %s,

            Your mock interview report is ready.

            Interview Details:
            Mentor: %s
            Interview Type: %s
            Interview Date & Time: %s

            Summary:
            %s

            Strengths:
            %s

            Weaknesses:
            %s

            Recommendations:
            %s

            CareerFit Community
            """.formatted(
                student.getFullName(),
                mentor.getFullName(),
                mockInterview.getInterviewType(),
                mockInterview.getScheduledAt(),
                report.getSummary(),
                report.getStrengths(),
                report.getWeaknesses(),
                report.getRecommendations()
        );

        sendEmail(student.getEmail(), subject, body);
    }
}