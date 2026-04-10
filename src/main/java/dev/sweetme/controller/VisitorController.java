package dev.sweetme.controller;

import dev.sweetme.service.VisitorService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class VisitorController {

    private final VisitorService visitorService;

    @PostMapping("/api/visitors/ping")
    public ResponseEntity<Void> ping(HttpServletRequest request) {
        String ip = resolveIp(request);
        visitorService.record(ip);
        return ResponseEntity.ok().build();
    }

    private String resolveIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @GetMapping("/api/admin/visitors")
    public Map<String, Long> getStats() {
        return visitorService.getStats();
    }

    @GetMapping("/api/admin/visitors/daily")
    public List<Map<String, Object>> getDaily(@RequestParam(defaultValue = "14") int days) {
        return visitorService.getDailyStats(days);
    }
}
