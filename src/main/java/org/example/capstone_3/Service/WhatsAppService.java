package org.example.capstone_3.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.example.capstone_3.Model.LearningGroup;
import org.example.capstone_3.Model.Mentor;
import org.example.capstone_3.Model.MockInterview;
import org.example.capstone_3.Model.Student;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WhatsAppService {
    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.whatsapp-from}")
    private String from;

    public void sendWhatsApp(String toPhone, String message) {
        Twilio.init(accountSid, authToken);

        Message.creator(
                new PhoneNumber("whatsapp:+" + toPhone),
                new PhoneNumber(from),
                message
        ).create();
    }

    public void sendInviteMessage(Student invitedStudent, LearningGroup learningGroup, Student inviter) {
        String message = "Hello " + invitedStudent.getFullName() + "\n\n"
                + inviter.getFullName() + " has invited you to join the private group: "
                + learningGroup.getName() + "\n"
                + "Focus Area: " + learningGroup.getFocusArea() + "\n"
                + "Use this code to join: " + learningGroup.getCode() + "\n\n"
                + "CareerFit Community";

        sendWhatsApp(invitedStudent.getPhoneNumber(), message);
    }

    public void sendInterviewReminderToStudent(Student student, MockInterview mockInterview) {
        String message = "Hello " + student.getFullName() + "\n\n"
                + "Reminder: You have a mock interview coming up\n\n"
                + "Interview Details:\n"
                + "Type: " + mockInterview.getInterviewType() + "\n"
                + "Date & Time: " + mockInterview.getScheduledAt() + "\n"
                + "Duration: " + mockInterview.getDurationMinutes() + " minutes\n"
                + "Meeting Link: " + mockInterview.getUrl() + "\n\n"
                + "Good luck\n\n"
                + "CareerFit Community";

        sendWhatsApp(student.getPhoneNumber(), message);
    }

    public void sendInterviewRequestToMentor(Mentor mentor, Student student, MockInterview mockInterview) {
        String message = "Hello " + mentor.getFullName() + "\n\n"
                + "You have a new mock interview request!\n\n"
                + "Student: " + student.getFullName() + "\n"
                + "Interview Type: " + mockInterview.getInterviewType() + "\n"
                + "Date & Time: " + mockInterview.getScheduledAt() + "\n"
                + "Duration: " + mockInterview.getDurationMinutes() + " minutes\n\n"
                + "Please confirm or reject the request.\n\n"
                + "CareerFit Community";

        sendWhatsApp(mentor.getPhoneNumber(), message);
    }
}
