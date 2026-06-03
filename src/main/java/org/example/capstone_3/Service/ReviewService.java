package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiException;
import org.example.capstone_3.DTO.IN.ReviewDTOIN;
import org.example.capstone_3.DTO.OUT.ReviewDTOOUT;
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

    public void create(Integer studentId, Integer mentorId, Integer mockInterviewId, ReviewDTOIN dto) {

        Student student = studentRepository.findStudentById(studentId);

        if (student == null) {
            throw new ApiException("Student with id " + studentId + " not found");
        }

        Mentor mentor = mentorRepository.findMentorById(mentorId);

        if (mentor == null) {
            throw new ApiException("Mentor with id " + mentorId + " not found");
        }

        MockInterview mockInterview = mockInterviewRepository.findMockInterviewById(mockInterviewId);

        if (mockInterview == null) {
            throw new ApiException("Mock interview with id " + mockInterviewId + " not found");
        }

        if (mockInterview.getStudent() == null || !mockInterview.getStudent().getId().equals(studentId)) {
            throw new ApiException("This mock interview does not belong to this student");
        }

        if (mockInterview.getMentor() == null || !mockInterview.getMentor().getId().equals(mentorId)) {
            throw new ApiException("This mock interview does not belong to this mentor");
        }

        if (!mockInterview.getStatus().equals("COMPLETE")) {
            throw new ApiException("Student can review mentor only after completed mock interview");
        }

        if (reviewRepository.findReviewByMockInterviewId(mockInterviewId) != null) {
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
    }

    public ReviewDTOOUT getById(Integer id) {

        Review review = reviewRepository.findReviewById(id);

        if (review == null) {
            throw new ApiException("Review with id " + id + " not found");
        }

        return toDtoOut(review);
    }

    public List<ReviewDTOOUT> getAll() {

        List<Review> reviews = reviewRepository.findAll();

        List<ReviewDTOOUT> reviewDTOOUTS = new ArrayList<>();

        for (Review review : reviews) {
            reviewDTOOUTS.add(toDtoOut(review));
        }

        return reviewDTOOUTS;
    }

    public List<ReviewDTOOUT> getReviewsByMentorId(Integer mentorId) {

        Mentor mentor = mentorRepository.findMentorById(mentorId);

        if (mentor == null) {
            throw new ApiException("Mentor with id " + mentorId + " not found");
        }

        List<Review> reviews = reviewRepository.findReviewsByMentorId(mentorId);

        List<ReviewDTOOUT> reviewDTOOUTS = new ArrayList<>();

        for (Review review : reviews) {
            reviewDTOOUTS.add(toDtoOut(review));
        }

        return reviewDTOOUTS;
    }

    public void update(Integer id, ReviewDTOIN dto) {

        Review review = reviewRepository.findReviewById(id);

        if (review == null) {
            throw new ApiException("Review with id " + id + " not found");
        }

        review.setRating(dto.getRating());
        review.setComment(dto.getComment());

        reviewRepository.save(review);

        updateMentorRating(review.getMentor());
    }

    public void delete(Integer id) {

        Review review = reviewRepository.findReviewById(id);

        if (review == null) {
            throw new ApiException("Review with id " + id + " not found");
        }

        Mentor mentor = review.getMentor();

        reviewRepository.delete(review);

        updateMentorRating(mentor);
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

        double averageRating = (double) totalRating / reviews.size();

        mentor.setRating(averageRating);

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