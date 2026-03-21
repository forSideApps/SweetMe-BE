package dev.sweetme.repository;

import dev.sweetme.domain.VisitorLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface VisitorLogRepository extends JpaRepository<VisitorLog, Long> {
    boolean existsByIpAndVisitDate(String ip, LocalDate visitDate);
    long countByVisitDate(LocalDate visitDate);
}
