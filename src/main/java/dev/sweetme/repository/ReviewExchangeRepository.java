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

    // 수락된 교환: 대상 유저(target 리뷰 소유자)가 requester 링크를 볼 수 있는지
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM ReviewExchange e " +
           "JOIN Review r ON r.id = e.targetReviewId " +
           "WHERE e.requesterReviewId = :requesterReviewId AND r.memberUsername = :username " +
           "AND e.status = dev.sweetme.domain.enums.ExchangeStatus.ACCEPTED")
    boolean hasAccessToRequesterLink(@Param("requesterReviewId") Long requesterReviewId, @Param("username") String username);

    // 수락된 교환: 요청자(requester 리뷰 소유자)가 target 링크를 볼 수 있는지
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM ReviewExchange e " +
           "JOIN Review r ON r.id = e.requesterReviewId " +
           "WHERE e.targetReviewId = :targetReviewId AND r.memberUsername = :username " +
           "AND e.status = dev.sweetme.domain.enums.ExchangeStatus.ACCEPTED")
    boolean hasAccessAsRequester(@Param("targetReviewId") Long targetReviewId, @Param("username") String username);
}
