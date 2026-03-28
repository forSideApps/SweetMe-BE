package dev.sweetme.service;

import dev.sweetme.domain.Review;
import dev.sweetme.domain.ReviewComment;
import dev.sweetme.dto.CommentRequest;
import dev.sweetme.repository.ReviewCommentRepository;
import dev.sweetme.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewCommentService {

    private final ReviewCommentRepository reviewCommentRepository;
    private final ReviewRepository reviewRepository;

    private Review findReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    }

    @Transactional
    public void addComment(Long reviewId, CommentRequest request, boolean isAdmin, String memberUsername) {
        Review review = findReviewById(reviewId);
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
        if (!isAdmin && (memberUsername == null || !memberUsername.equals(comment.getMemberUsername()))) {
            throw new SecurityException("수정 권한이 없습니다.");
        }
        comment.updateContent(content);
    }

    @Transactional
    public void deleteComment(Long commentId, boolean isAdmin, String memberUsername) {
        ReviewComment comment = reviewCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        if (!isAdmin && (memberUsername == null || !memberUsername.equals(comment.getMemberUsername()))) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }
        reviewCommentRepository.delete(comment);
    }
}
