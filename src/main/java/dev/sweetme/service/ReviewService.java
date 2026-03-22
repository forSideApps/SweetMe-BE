package dev.sweetme.service;

import dev.sweetme.domain.Review;
import dev.sweetme.domain.enums.CareerLevel;
import dev.sweetme.domain.enums.ReviewJobCategory;
import dev.sweetme.domain.enums.ReviewStatus;
import dev.sweetme.domain.enums.ReviewType;
import dev.sweetme.domain.ReviewComment;
import dev.sweetme.dto.CommentRequest;
import dev.sweetme.dto.ReviewRequest;
import dev.sweetme.dto.ReviewUpdateRequest;
import dev.sweetme.repository.ReviewCommentRepository;
import dev.sweetme.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewCommentRepository reviewCommentRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${app.page.review-size:15}")
    private int pageSize;

    public Page<Review> findReviews(String type, String status, String jobCategory, String careerLevel, String keyword, int page) {
        var pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        ReviewType t = parseEnum(type, ReviewType.class);
        ReviewStatus s = parseEnum(status, ReviewStatus.class);
        ReviewJobCategory jc = parseEnum(jobCategory, ReviewJobCategory.class);
        CareerLevel cl = parseEnum(careerLevel, CareerLevel.class);
        String kw = parseKeyword(keyword);
        return reviewRepository.search(t, s, jc, cl, kw, pageable);
    }

    public Review findById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    }

    @Transactional
    public Review create(ReviewRequest request, String memberUsername) {
        String hash = (request.getPassword() != null && !request.getPassword().isBlank())
                ? passwordEncoder.encode(request.getPassword()) : null;
        Review review = Review.builder()
                .type(ReviewType.valueOf(request.getType()))
                .jobCategory(ReviewJobCategory.valueOf(request.getJobCategory()))
                .careerLevel(CareerLevel.valueOf(request.getCareerLevel()))
                .title(request.getTitle())
                .content(request.getContent())
                .authorName(request.getAuthorName())
                .contactInfo(request.getContactInfo())
                .portfolioLink(request.getPortfolioLink())
                .passwordHash(hash)
                .memberUsername(memberUsername)
                .build();
        return reviewRepository.save(review);
    }

    public boolean verifyPassword(Long id, String rawPassword) {
        String hash = findById(id).getPasswordHash();
        if (hash == null) return false;
        return passwordEncoder.matches(rawPassword, hash);
    }

    public String getPortfolioLink(Long id, String rawPassword, boolean isAdmin, String memberUsername) {
        Review review = findById(id);
        boolean isOwner = memberUsername != null && memberUsername.equals(review.getMemberUsername());
        if (!isAdmin && !isOwner) {
            if (review.getPasswordHash() == null || !passwordEncoder.matches(rawPassword, review.getPasswordHash())) {
                throw new SecurityException("비밀번호가 올바르지 않습니다.");
            }
        }
        return review.getPortfolioLink();
    }

    @Transactional
    public Review update(Long id, ReviewUpdateRequest request) {
        Review review = findById(id);
        review.update(
                ReviewType.valueOf(request.getType()),
                ReviewJobCategory.valueOf(request.getJobCategory()),
                CareerLevel.valueOf(request.getCareerLevel()),
                request.getTitle(),
                request.getContent(),
                request.getContactInfo(),
                request.getPortfolioLink()
        );
        return review;
    }

    @Transactional
    public void markDone(Long id) {
        findById(id).markDone();
    }

    @Transactional
    public void markPending(Long id) {
        findById(id).markPending();
    }

    @Transactional
    public void incrementView(Long id) {
        findById(id).incrementViewCount();
    }

    @Transactional
    public void addComment(Long reviewId, CommentRequest request, boolean isAdmin, String memberUsername) {
        Review review = findById(reviewId);
        ReviewComment comment = ReviewComment.builder()
                .review(review)
                .authorName(isAdmin ? "운영자" : request.getAuthorName())
                .content(request.getContent())
                .isAdmin(isAdmin)
                .memberUsername(isAdmin ? null : memberUsername)
                .build();
        reviewCommentRepository.save(comment);
    }

    @Transactional
    public void updateComment(Long commentId, String content, boolean isAdmin, String memberUsername) {
        ReviewComment comment = reviewCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        if (!isAdmin) {
            if (memberUsername == null || !memberUsername.equals(comment.getMemberUsername())) {
                throw new SecurityException("수정 권한이 없습니다.");
            }
        }
        comment.updateContent(content);
    }

    @Transactional
    public void deleteComment(Long commentId, boolean isAdmin, String memberUsername) {
        ReviewComment comment = reviewCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        if (!isAdmin) {
            if (memberUsername == null || !memberUsername.equals(comment.getMemberUsername())) {
                throw new SecurityException("삭제 권한이 없습니다.");
            }
        }
        reviewCommentRepository.delete(comment);
    }

    @Transactional
    public void delete(Long id) {
        reviewRepository.deleteById(id);
    }

    private <E extends Enum<E>> E parseEnum(String value, Class<E> clazz) {
        return (value != null && !value.isBlank()) ? Enum.valueOf(clazz, value) : null;
    }

    private String parseKeyword(String value) {
        return (value != null && !value.isBlank()) ? value : null;
    }
}
