package dev.sweetme.dto.response;

import dev.sweetme.domain.ReviewComment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReviewCommentDto {
    private final Long id;
    private final String authorName;
    private final String memberUsername;
    private final String content;
    private final LocalDateTime createdAt;
    private final boolean isAdmin;

    private ReviewCommentDto(ReviewComment c) {
        this.id = c.getId();
        this.authorName = c.getAuthorName();
        this.memberUsername = c.getMemberUsername();
        this.content = c.getContent();
        this.createdAt = c.getCreatedAt();
        this.isAdmin = c.getIsAdmin() != null && c.getIsAdmin();
    }

    public static ReviewCommentDto from(ReviewComment c) {
        return new ReviewCommentDto(c);
    }
}
