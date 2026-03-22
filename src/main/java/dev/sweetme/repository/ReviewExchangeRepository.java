package dev.sweetme.repository;

import dev.sweetme.domain.ReviewExchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewExchangeRepository extends JpaRepository<ReviewExchange, Long> {

    boolean existsByRequesterReviewIdAndTargetReviewId(Long requesterReviewId, Long targetReviewId);

    List<ReviewExchange> findByRequesterReviewId(Long requesterReviewId);

    @Query("SELECT e FROM ReviewExchange e WHERE e.targetReviewId IN :reviewIds ORDER BY e.createdAt DESC")
    List<ReviewExchange> findByTargetReviewIdIn(@Param("reviewIds") List<Long> reviewIds);

    @Query("SELECT e FROM ReviewExchange e WHERE e.requesterReviewId IN :reviewIds ORDER BY e.createdAt DESC")
    List<ReviewExchange> findByRequesterReviewIdIn(@Param("reviewIds") List<Long> reviewIds);

    // 내가 교환 대상이 된 경우: 상대방의 리뷰(requesterReviewId)를 보려면, 내 리뷰 중 targetReviewId인 게 있어야 함
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM ReviewExchange e " +
           "JOIN Review r ON r.id = e.targetReviewId " +
           "WHERE e.requesterReviewId = :requesterReviewId AND r.memberUsername = :username")
    boolean hasAccessToRequesterLink(@Param("requesterReviewId") Long requesterReviewId, @Param("username") String username);
}
