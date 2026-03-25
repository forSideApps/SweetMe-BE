package dev.sweetme.controller;

import dev.sweetme.service.VisitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class VisitorController {

    private final VisitorService visitorService;

    @PostMapping("/api/visitors/ping")
    public ResponseEntity<Void> ping() {
        visitorService.record();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/admin/visitors")
    public Map<String, Long> getStats() {
        return visitorService.getStats();
    }
}
