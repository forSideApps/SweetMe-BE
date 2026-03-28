package dev.sweetme.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.sweetme.domain.CommunityComment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    private Long id;
    private String authorName;
    private String memberUsername;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public static CommentDto from(CommunityComment comment) {
        String authorName = "admin".equals(comment.getMemberUsername()) ? "운영자" : comment.getAuthorName();
        return new CommentDto(
                comment.getId(),
                authorName,
                comment.getMemberUsername(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}
