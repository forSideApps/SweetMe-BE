package dev.sweetme.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.sweetme.domain.CommunityPost;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostSummaryDto {

    private Long id;
    private String category;
    private String categoryDisplay;
    private String title;
    private String content;
    private String authorName;
    private String memberUsername;
    private Integer viewCount;
    private int commentCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public static PostSummaryDto from(CommunityPost post) {
        String authorName = "admin".equals(post.getMemberUsername()) ? "운영자" : post.getAuthorName();
        return new PostSummaryDto(
                post.getId(),
                post.getCategory().name(),
                post.getCategory().getDisplayName(),
                post.getTitle(),
                post.getContent(),
                authorName,
                post.getMemberUsername(),
                post.getViewCount(),
                post.getCommentCount(),
                post.getCreatedAt()
        );
    }
}
