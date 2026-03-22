package dev.sweetme.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_exchange",
       uniqueConstraints = @UniqueConstraint(columnNames = {"requester_review_id", "target_review_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ReviewExchange {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_review_exchange")
    @SequenceGenerator(name = "seq_review_exchange", sequenceName = "SEQ_REVIEW_EXCHANGE", allocationSize = 1)
    private Long id;

    @Column(name = "requester_review_id", nullable = false)
    private Long requesterReviewId;

    @Column(name = "target_review_id", nullable = false)
    private Long targetReviewId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
