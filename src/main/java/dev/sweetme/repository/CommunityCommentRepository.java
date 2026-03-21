package dev.sweetme.repository;

import dev.sweetme.domain.CommunityComment;
import dev.sweetme.domain.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {

    List<CommunityComment> findByPostOrderByCreatedAtAsc(CommunityPost post);
}
