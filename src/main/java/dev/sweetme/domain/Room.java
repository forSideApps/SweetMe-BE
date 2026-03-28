package dev.sweetme.domain;

import dev.sweetme.domain.enums.JobRole;
import dev.sweetme.domain.enums.RoomStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "room")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Company company;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(name = "kakao_link", length = 500)
    private String kakaoLink;

    @Column(name = "password_hash", length = 100)
    private String passwordHash;

    @Column(name = "creator_nickname", nullable = false, length = 50)
    private String creatorNickname;

    @Column(name = "member_username", length = 50)
    private String memberUsername;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RoomStatus status = RoomStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_role", length = 20)
    private JobRole jobRole;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<RoomApplication> applications = new ArrayList<>();

    public void close() {
        this.status = RoomStatus.CLOSED;
    }

    public void update(String title, String description, String kakaoLink, JobRole jobRole) {
        this.title = title;
        this.description = description;
        this.kakaoLink = kakaoLink;
        this.jobRole = jobRole;
    }

    public void updateStatus(RoomStatus status) {
        this.status = status;
    }
}
