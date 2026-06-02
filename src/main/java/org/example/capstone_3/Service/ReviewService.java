package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiException;
import org.example.capstone_3.DTO.IN.ReviewDTOIN;
import org.example.capstone_3.DTO.OUT.ReviewDTOOUT;
import org.example.capstone_3.Model.Mentor;
import org.example.capstone_3.Model.Review;
import org.example.capstone_3.Model.Student;
import org.example.capstone_3.Repository.MentorRepository;
import org.example.capstone_3.Repository.ReviewRepository;
import org.example.capstone_3.Repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final StudentRepository studentRepository;
    private final MentorRepository mentorRepository;

    public ReviewDTOOUT create(ReviewDTOIN dto) {
        Review review = new Review();
        applyDto(review, dto);
        return toDtoOut(reviewRepository.save(review));
    }

    public ReviewDTOOUT getById(Integer id) {
        Review review = reviewRepository.findReviewById(id);
        if (review == null) {
            throw new ApiException("Review with id " + id + " not found");
        }
        return toDtoOut(review);
    }

    public List<ReviewDTOOUT> getAll() {
        return reviewRepository.findAll().stream().map(this::toDtoOut).toList();
    }

    public ReviewDTOOUT update(Integer id, ReviewDTOIN dto) {
        Review review = reviewRepository.findReviewById(id);
        if (review == null) {
            throw new ApiException("Review with id " + id + " not found");
        }
        applyDto(review, dto);
        return toDtoOut(reviewRepository.save(review));
    }

    public void delete(Integer id) {
        Review review = reviewRepository.findReviewById(id);
        if (review == null) {
            throw new ApiException("Review with id " + id + " not found");
        }
        reviewRepository.deleteById(id);
    }

    private void applyDto(Review review, ReviewDTOIN dto) {
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setCreatedAt(dto.getCreatedAt());
        review.setStudent(findStudent(dto.getStudentId()));
        review.setMentor(findMentor(dto.getMentorId()));
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

    private Mentor findMentor(Integer mentorId) {
        if (mentorId == null) {
            return null;
        }
        Mentor mentor = mentorRepository.findMentorById(mentorId);
        if (mentor == null) {
            throw new ApiException("Mentor with id " + mentorId + " not found");
        }
        return mentor;
    }

    private ReviewDTOOUT toDtoOut(Review review) {
        Integer studentId = review.getStudent() != null ? review.getStudent().getId() : null;
        Integer mentorId = review.getMentor() != null ? review.getMentor().getId() : null;
        return new ReviewDTOOUT(
                review.getId(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt(),
                studentId,
                mentorId
        );
    }
}
