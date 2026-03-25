package dev.sweetme.service;

import dev.sweetme.domain.VisitorLog;
import dev.sweetme.repository.VisitorLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VisitorService {

    private final VisitorLogRepository visitorLogRepository;

    @Transactional
    public void record() {
        visitorLogRepository.save(new VisitorLog(UUID.randomUUID().toString(), LocalDate.now()));
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getStats() {
        long today = visitorLogRepository.countByVisitDate(LocalDate.now());
        long total = visitorLogRepository.count();
        return Map.of("today", today, "total", total);
    }
}
