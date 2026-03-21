package dev.sweetme.controller;

import dev.sweetme.domain.enums.PostCategory;
import dev.sweetme.dto.CommentRequest;
import dev.sweetme.dto.CommunityPostRequest;
import dev.sweetme.dto.response.PostDetailDto;
import dev.sweetme.dto.response.PostSummaryDto;
import dev.sweetme.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityApiController {

    private final CommunityService communityService;

    @Value("${app.admin.password}")
    private String adminPassword;

    @GetMapping
    public Page<PostSummaryDto> getPosts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page) {
        PostCategory postCategory = null;
        if (category != null && !category.isBlank()) {
            postCategory = PostCategory.valueOf(category);
        }
        return communityService.findPosts(postCategory, keyword, page)
                .map(PostSummaryDto::from);
    }

    @GetMapping("/{id}")
    public PostDetailDto getPost(@PathVariable Long id) {
        return PostDetailDto.from(communityService.findById(id));
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<Void> incrementView(@PathVariable Long id) {
        communityService.incrementView(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<?> createPost(
            @RequestHeader(value = "X-Admin-Key", required = false) String adminKey,
            @RequestBody CommunityPostRequest request) {
        if (PostCategory.NOTICE == request.getCategory()) {
            if (adminKey == null || !adminKey.equals(adminPassword)) {
                return ResponseEntity.status(403).body(Map.of("message", "공지사항은 어드민만 작성할 수 있습니다."));
            }
        }
        var post = communityService.createPost(request);
        return ResponseEntity.ok(Map.of("id", post.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @RequestHeader(value = "X-Admin-Key", required = false) String adminKey) {
        if (adminKey == null || !adminKey.equals(adminPassword)) {
            return ResponseEntity.status(403).build();
        }
        communityService.deletePost(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<Void> addComment(
            @PathVariable Long id,
            @RequestBody CommentRequest request) {
        communityService.addComment(id, request);
        return ResponseEntity.ok().build();
    }
}
