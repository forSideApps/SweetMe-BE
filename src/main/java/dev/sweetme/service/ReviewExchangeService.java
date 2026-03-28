package dev.sweetme.service;

import dev.sweetme.domain.Review;
import dev.sweetme.domain.ReviewExchange;
import dev.sweetme.domain.enums.ExchangeStatus;
import dev.sweetme.repository.ReviewExchangeRepository;
import dev.sweetme.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewExchangeService {

    private final ReviewExchangeRepository exchangeRepository;
    private final ReviewRepository reviewRepository;

    private Review findReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    }

    private ReviewExchange findExchangeById(Long id) {
        return exchangeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("서로보기 요청을 찾을 수 없습니다."));
    }

    @Transactional
    public void createExchangeRequest(Long targetReviewId, Long myReviewId, String sessionUsername) {
        Review myReview = findReviewById(myReviewId);
        if (!sessionUsername.equals(myReview.getMemberUsername())) {
            throw new SecurityException("본인의 글만 제공할 수 있습니다.");
        }
        Review targetReview = findReviewById(targetReviewId);
        if (sessionUsername.equals(targetReview.getMemberUsername())) {
            throw new IllegalArgumentException("자신의 글과는 교환할 수 없습니다.");
        }
        if (exchangeRepository.existsByRequesterReviewIdAndTargetReviewId(myReviewId, targetReviewId)) {
            throw new IllegalArgumentException("이미 서로보기 요청을 보냈습니다.");
        }
        exchangeRepository.save(ReviewExchange.builder()
                .requesterReviewId(myReviewId)
                .targetReviewId(targetReviewId)
                .build());
    }

    @Transactional
    public void acceptExchange(Long exchangeId, String username) {
        ReviewExchange exchange = findExchangeById(exchangeId);
        Review targetReview = findReviewById(exchange.getTargetReviewId());
        if (!username.equals(targetReview.getMemberUsername())) {
            throw new SecurityException("수락 권한이 없습니다.");
        }
        if (exchange.getStatus() != ExchangeStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }
        exchange.accept();
    }

    @Transactional
    public void rejectExchange(Long exchangeId, String username) {
        ReviewExchange exchange = findExchangeById(exchangeId);
        if (exchange.getStatus() != ExchangeStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }
        Review targetReview = findReviewById(exchange.getTargetReviewId());
        if (!username.equals(targetReview.getMemberUsername())) {
            throw new SecurityException("거절 권한이 없습니다.");
        }
        exchange.reject();
    }

    @Transactional
    public void cancelExchange(Long exchangeId, String username) {
        ReviewExchange exchange = findExchangeById(exchangeId);
        Review requesterReview = findReviewById(exchange.getRequesterReviewId());
        if (!username.equals(requesterReview.getMemberUsername())) {
            throw new SecurityException("취소 권한이 없습니다.");
        }
        if (exchange.getStatus() != ExchangeStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }
        exchange.cancel();
    }
}
