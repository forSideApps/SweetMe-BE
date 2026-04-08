package dev.sweetme.repository;

import dev.sweetme.domain.VisitorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface VisitorLogRepository extends JpaRepository<VisitorLog, Long> {
    long countByVisitDate(LocalDate visitDate);

    @Query("SELECT v.visitDate, COUNT(v) FROM VisitorLog v WHERE v.visitDate >= :from GROUP BY v.visitDate ORDER BY v.visitDate")
    List<Object[]> findDailyCountsSince(@Param("from") LocalDate from);
}
