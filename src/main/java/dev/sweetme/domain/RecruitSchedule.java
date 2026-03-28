package dev.sweetme.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "recruit_schedule")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RecruitSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String company;

    @Column(name = "hire_type", nullable = false, length = 50)
    private String hireType;

    @Column(nullable = false, length = 50)
    private String stage;

    @Column(name = "announce_date", nullable = false)
    private LocalDate announceDate;

    @Column(name = "announce_time", length = 5)
    private String announceTime;

    @Column(length = 1000)
    private String notes;

    @Column(name = "submitted_by", length = 50)
    private String submittedBy;

    @Column(name = "submitter_name", nullable = false, length = 50)
    private String submitterName;

    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
