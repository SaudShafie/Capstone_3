package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiException;
import org.example.capstone_3.DTO.IN.ReviewDTOIN;
import org.example.capstone_3.DTO.OUT.ReviewDTOOUT;
import org.example.capstone_3.DTO.OUT.ReviewableMockInterviewDTOOut;
import org.example.capstone_3.Model.Mentor;
import org.example.capstone_3.Model.MockInterview;
import org.example.capstone_3.Model.Review;
import org.example.capstone_3.Model.Student;
import org.example.capstone_3.Repository.MentorRepository;
import org.example.capstone_3.Repository.MockInterviewRepository;
import org.example.capstone_3.Repository.ReviewRepository;
import org.example.capstone_3.Repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final StudentRepository studentRepository;
    private final MentorRepository mentorRepository;
    private final MockInterviewRepository mockInterviewRepository;

    public ReviewDTOOUT createByStudent(Integer studentId, Integer mockInterviewId, ReviewDTOIN dto) {
        Student student = findStudent(studentId);
        MockInterview mockInterview = findMockInterview(mockInterviewId);
        validateReviewableMockInterview(student, mockInterview);

        Mentor mentor = mockInterview.getMentor();
        return saveReview(student, mentor, mockInterview, dto);
    }

    public ReviewDTOOUT create(Integer studentId, Integer mentorId, Integer mockInterviewId, ReviewDTOIN dto) {
        Student student = findStudent(studentId);
        Mentor mentor = findMentor(mentorId);
        MockInterview mockInterview = findMockInterview(mockInterviewId);

        validateReviewableMockInterview(student, mockInterview);

        if (!mockInterview.getMentor().getId().equals(mentorId)) {
            throw new ApiException("This mock interview does not belong to this mentor");
        }

        return saveReview(student, mentor, mockInterview, dto);
    }

    public ReviewDTOOUT getById(Integer id) {
        Review review = findReview(id);
        return toDtoOut(review);
    }

    public List<ReviewDTOOUT> getAll() {
        List<ReviewDTOOUT> reviewDTOOUTS = new ArrayList<>();
        for (Review review : reviewRepository.findAll()) {
            reviewDTOOUTS.add(toDtoOut(review));
        }
        return reviewDTOOUTS;
    }

    public List<ReviewDTOOUT> getReviewsByMentorId(Integer mentorId) {
        findMentor(mentorId);
        List<ReviewDTOOUT> reviewDTOOUTS = new ArrayList<>();
        for (Review review : reviewRepository.findReviewsByMentorId(mentorId)) {
            reviewDTOOUTS.add(toDtoOut(review));
        }
        return reviewDTOOUTS;
    }

    public List<ReviewDTOOUT> getReviewsByStudentId(Integer studentId) {
        findStudent(studentId);
        List<ReviewDTOOUT> reviewDTOOUTS = new ArrayList<>();
        for (Review review : reviewRepository.findReviewsByStudentId(studentId)) {
            reviewDTOOUTS.add(toDtoOut(review));
        }
        return reviewDTOOUTS;
    }

    public List<ReviewableMockInterviewDTOOut> getReviewableMockInterviews(Integer studentId) {
        findStudent(studentId);

        List<MockInterview> completedInterviews = mockInterviewRepository
                .findMockInterviewsByStudentIdAndInterviewModeAndStatus(studentId, "MENTOR", "COMPLETE");

        List<ReviewableMockInterviewDTOOut> result = new ArrayList<>();
        for (MockInterview interview : completedInterviews) {
            Review existingReview = reviewRepository.findReviewByMockInterviewId(interview.getId());
            Mentor mentor = interview.getMentor();

            result.add(new ReviewableMockInterviewDTOOut(
                    interview.getId(),
                    mentor != null ? mentor.getId() : null,
                    mentor != null ? mentor.getFullName() : null,
                    interview.getInterviewType(),
                    interview.getScheduledAt(),
                    interview.getScore(),
                    existingReview != null
            ));
        }
        return result;
    }

    public ReviewDTOOUT updateByStudent(Integer studentId, Integer reviewId, ReviewDTOIN dto) {
        Review review = findReview(reviewId);
        assertReviewOwner(studentId, review);
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        reviewRepository.save(review);
        updateMentorRating(review.getMentor());
        return toDtoOut(review);
    }

    public void update(Integer id, ReviewDTOIN dto) {
        Review review = findReview(id);
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        reviewRepository.save(review);
        updateMentorRating(review.getMentor());
    }

    public void delete(Integer id) {
        Review review = findReview(id);
        Mentor mentor = review.getMentor();
        reviewRepository.delete(review);
        updateMentorRating(mentor);
    }

    private ReviewDTOOUT saveReview(Student student, Mentor mentor, MockInterview mockInterview, ReviewDTOIN dto) {
        if (reviewRepository.findReviewByMockInterviewId(mockInterview.getId()) != null) {
            throw new ApiException("This mock interview already has a review");
        }

        Review review = new Review();
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setCreatedAt(LocalDateTime.now());
        review.setStudent(student);
        review.setMentor(mentor);
        review.setMockInterview(mockInterview);

        reviewRepository.save(review);
        updateMentorRating(mentor);
        return toDtoOut(review);
    }

    private void validateReviewableMockInterview(Student student, MockInterview mockInterview) {
        if (!"MENTOR".equals(mockInterview.getInterviewMode())) {
            throw new ApiException("Only completed mentor mock interviews can be reviewed");
        }

        if (mockInterview.getStudent() == null || !mockInterview.getStudent().getId().equals(student.getId())) {
            throw new ApiException("This mock interview does not belong to this student");
        }

        if (mockInterview.getMentor() == null) {
            throw new ApiException("This mock interview is not linked to a mentor");
        }

        if (!"COMPLETE".equals(mockInterview.getStatus())) {
            throw new ApiException("Student can review mentor only after completed mock interview");
        }
    }

    private void assertReviewOwner(Integer studentId, Review review) {
        if (review.getStudent() == null || !review.getStudent().getId().equals(studentId)) {
            throw new ApiException("This review does not belong to this student");
        }
    }

    private Student findStudent(Integer studentId) {
        Student student = studentRepository.findStudentById(studentId);
        if (student == null) {
            throw new ApiException("Student with id " + studentId + " not found");
        }
        return student;
    }

    private Mentor findMentor(Integer mentorId) {
        Mentor mentor = mentorRepository.findMentorById(mentorId);
        if (mentor == null) {
            throw new ApiException("Mentor with id " + mentorId + " not found");
        }
        return mentor;
    }

    private MockInterview findMockInterview(Integer mockInterviewId) {
        MockInterview mockInterview = mockInterviewRepository.findMockInterviewById(mockInterviewId);
        if (mockInterview == null) {
            throw new ApiException("Mock interview with id " + mockInterviewId + " not found");
        }
        return mockInterview;
    }

    private Review findReview(Integer id) {
        Review review = reviewRepository.findReviewById(id);
        if (review == null) {
            throw new ApiException("Review with id " + id + " not found");
        }
        return review;
    }

    private void updateMentorRating(Mentor mentor) {
        if (mentor == null) {
            return;
        }

        List<Review> reviews = reviewRepository.findReviewsByMentorId(mentor.getId());

        if (reviews == null || reviews.isEmpty()) {
            mentor.setRating(0.0);
            mentorRepository.save(mentor);
            return;
        }

        int totalRating = 0;
        for (Review review : reviews) {
            totalRating = totalRating + review.getRating();
        }

        mentor.setRating((double) totalRating / reviews.size());
        mentorRepository.save(mentor);
    }

    private ReviewDTOOUT toDtoOut(Review review) {
        Integer studentId = review.getStudent() != null ? review.getStudent().getId() : null;
        Integer mentorId = review.getMentor() != null ? review.getMentor().getId() : null;
        Integer mockInterviewId = review.getMockInterview() != null ? review.getMockInterview().getId() : null;

        return new ReviewDTOOUT(
                review.getId(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt(),
                studentId,
                mentorId,
                mockInterviewId
        );
    }
}
