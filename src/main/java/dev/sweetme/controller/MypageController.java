package dev.sweetme.controller;

import dev.sweetme.domain.Review;
import dev.sweetme.domain.ReviewExchange;
import dev.sweetme.dto.response.PostSummaryDto;
import dev.sweetme.dto.response.ReviewSummaryDto;
import dev.sweetme.dto.response.RoomSummaryDto;
import dev.sweetme.repository.CommunityPostRepository;
import dev.sweetme.repository.ReviewExchangeRepository;
import dev.sweetme.repository.ReviewRepository;
import dev.sweetme.repository.RoomApplicationRepository;
import dev.sweetme.repository.RoomRepository;
import dev.sweetme.repository.MemberRepository;
import dev.sweetme.service.OciStorageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class MypageController extends BaseApiController {

    private final RoomRepository roomRepository;
    private final ReviewRepository reviewRepository;
    private final CommunityPostRepository communityPostRepository;
    private final RoomApplicationRepository roomApplicationRepository;
    private final ReviewExchangeRepository reviewExchangeRepository;
    private final MemberRepository memberRepository;
    private final OciStorageService ociStorageService;

    @GetMapping("/me/rooms")
    public ResponseEntity<?> myRooms(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        String username = session != null ? (String) session.getAttribute("member_username") : null;
        if (username == null) return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        List<RoomSummaryDto> rooms = roomRepository.findByMemberUsernameOrderByCreatedAtDesc(username)
                .stream().map(r -> RoomSummaryDto.from(r, ociStorageService.getLogoBaseUrl())).toList();
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/me/reviews")
    public ResponseEntity<?> myReviews(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        String username = session != null ? (String) session.getAttribute("member_username") : null;
        if (username == null) return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        List<ReviewSummaryDto> reviews = reviewRepository.findByMemberUsernameOrderByCreatedAtDesc(username)
                .stream().map(ReviewSummaryDto::from).toList();
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/me/applications")
    public ResponseEntity<?> myApplications(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        String username = session != null ? (String) session.getAttribute("member_username") : null;
        if (username == null) return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        List<MyApplicationDto> apps = roomApplicationRepository.findByMemberUsernameOrderByCreatedAtDesc(username)
                .stream().map(a -> new MyApplicationDto(
                        a.getId(),
                        a.getRoom().getId(),
                        a.getRoom().getTitle(),
                        a.getRoom().getCompany().getName(),
                        a.getStatus().name(),
                        a.getStatus().getDisplayName(),
                        a.getCreatedAt()
                )).toList();
        return ResponseEntity.ok(apps);
    }

    @GetMapping("/me/posts")
    public ResponseEntity<?> myPosts(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        String username = session != null ? (String) session.getAttribute("member_username") : null;
        if (username == null) return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        List<PostSummaryDto> posts = communityPostRepository.findByMemberUsernameOrderByCreatedAtDesc(username)
                .stream().map(PostSummaryDto::from).toList();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/me/exchanges")
    public ResponseEntity<?> myExchanges(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        String username = session != null ? (String) session.getAttribute("member_username") : null;
        if (username == null) return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));

        List<Long> myReviewIds = reviewRepository.findByMemberUsernameOrderByCreatedAtDesc(username)
                .stream().map(Review::getId).toList();
        if (myReviewIds.isEmpty()) return ResponseEntity.ok(List.of());

        List<ReviewExchange> received = reviewExchangeRepository.findByTargetReviewIdIn(myReviewIds);
        List<ReviewExchange> sent = reviewExchangeRepository.findByRequesterReviewIdIn(myReviewIds);

        List<Long> allReviewIds = new ArrayList<>();
        received.forEach(e -> { allReviewIds.add(e.getRequesterReviewId()); allReviewIds.add(e.getTargetReviewId()); });
        sent.forEach(e -> { allReviewIds.add(e.getRequesterReviewId()); allReviewIds.add(e.getTargetReviewId()); });

        Map<Long, Review> reviewMap = reviewRepository.findAllById(allReviewIds)
                .stream().collect(Collectors.toMap(Review::getId, r -> r));

        List<ExchangeDto> result = new ArrayList<>();
        received.forEach(e -> {
            Review myReview = reviewMap.get(e.getTargetReviewId());
            Review theirReview = reviewMap.get(e.getRequesterReviewId());
            if (myReview != null && theirReview != null) {
                result.add(new ExchangeDto(e.getId(), "RECEIVED",
                        myReview.getId(), myReview.getTitle(),
                        theirReview.getId(), theirReview.getTitle(),
                        theirReview.getMemberUsername(), e.getCreatedAt(),
                        e.getStatus().name()));
            }
        });
        sent.forEach(e -> {
            Review myReview = reviewMap.get(e.getRequesterReviewId());
            Review theirReview = reviewMap.get(e.getTargetReviewId());
            if (myReview != null && theirReview != null) {
                result.add(new ExchangeDto(e.getId(), "SENT",
                        myReview.getId(), myReview.getTitle(),
                        theirReview.getId(), theirReview.getTitle(),
                        theirReview.getMemberUsername(), e.getCreatedAt(),
                        e.getStatus().name()));
            }
        });
        result.sort(Comparator.comparing(ExchangeDto::createdAt).reversed());
        return ResponseEntity.ok(result);
    }

    @PutMapping("/profile")
    @Transactional
    public ResponseEntity<?> updateProfile(@RequestBody ProfileRequest req, HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        String username = session != null ? (String) session.getAttribute("member_username") : null;
        if (username == null) return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        memberRepository.findByUsername(username).ifPresent(m -> m.updateProfile(
                req.jobRole(), req.careerLevel(), req.algoGrade()));
        return ResponseEntity.ok(Map.of("message", "프로필이 업데이트되었습니다."));
    }

    public record MyApplicationDto(
        Long id, Long roomId, String roomTitle, String themeName,
        String status, String statusDisplay, java.time.LocalDateTime createdAt
    ) {}

    public record ExchangeDto(
        Long id, String direction,
        Long myReviewId, String myReviewTitle,
        Long theirReviewId, String theirReviewTitle,
        String theirUsername, java.time.LocalDateTime createdAt,
        String status
    ) {}

    public record ProfileRequest(String jobRole, String careerLevel, String algoGrade) {}
}
