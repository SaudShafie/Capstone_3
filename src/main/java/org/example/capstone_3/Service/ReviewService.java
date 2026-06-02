package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiException;
import org.example.capstone_3.DTO.IN.ReviewDTOIN;
import org.example.capstone_3.DTO.OUT.ReviewDTOOUT;
import org.example.capstone_3.Model.Review;
import org.example.capstone_3.Repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewDTOOUT create(ReviewDTOIN dto) {
        Review review = new Review();
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
        return toDtoOut(reviewRepository.save(review));
    }

    public void delete(Integer id) {
        Review review = reviewRepository.findReviewById(id);
        if (review == null) {
            throw new ApiException("Review with id " + id + " not found");
        }
        reviewRepository.deleteById(id);
    }

    private ReviewDTOOUT toDtoOut(Review review) {
        return new ReviewDTOOUT(review.getId());
    }
}
