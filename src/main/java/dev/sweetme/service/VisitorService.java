package dev.sweetme.service;

import dev.sweetme.domain.VisitorLog;
import dev.sweetme.repository.VisitorLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VisitorService {

    private final VisitorLogRepository visitorLogRepository;

    @Transactional
    public void record(String ip) {
        try {
            visitorLogRepository.save(new VisitorLog(ip, LocalDate.now()));
        } catch (DataIntegrityViolationException e) {
            // 동일 IP가 오늘 이미 방문한 경우 무시
            log.debug("Duplicate visitor ignored: ip={}", ip);
        }
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getStats() {
        long today = visitorLogRepository.countByVisitDate(LocalDate.now());
        long total = visitorLogRepository.count();
        return Map.of("today", today, "total", total);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDailyStats(int days) {
        LocalDate from = LocalDate.now().minusDays(days - 1);
        List<Object[]> rows = visitorLogRepository.findDailyCountsSince(from);

        Map<LocalDate, Long> map = new LinkedHashMap<>();
        for (int i = days - 1; i >= 0; i--) {
            map.put(LocalDate.now().minusDays(i), 0L);
        }
        for (Object[] row : rows) {
            map.put((LocalDate) row[0], (Long) row[1]);
        }

        return map.entrySet().stream()
                .map(e -> Map.<String, Object>of("date", e.getKey().toString(), "count", e.getValue()))
                .collect(Collectors.toList());
    }
}
