package dev.sweetme.repository;

import dev.sweetme.domain.RecruitSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RecruitScheduleRepository extends JpaRepository<RecruitSchedule, Long> {

    List<RecruitSchedule> findAllByOrderByAnnounceDateAscCreatedAtDesc();

    List<RecruitSchedule> findByAnnounceDateGreaterThanEqualOrderByAnnounceDateAscCreatedAtDesc(LocalDate date);

    List<RecruitSchedule> findBySubmittedByOrderByCreatedAtDesc(String submittedBy);
}
