package dev.sweetme.controller;

import dev.sweetme.dto.RecruitScheduleRequest;
import dev.sweetme.dto.response.RecruitScheduleDto;
import dev.sweetme.service.RecruitScheduleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class RecruitScheduleController extends BaseApiController {

    private final RecruitScheduleService service;

    @GetMapping
    public List<RecruitScheduleDto> getAll(
            @RequestParam(defaultValue = "false") boolean upcomingOnly) {
        return service.findAll(upcomingOnly).stream()
                .map(RecruitScheduleDto::from)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody RecruitScheduleRequest request,
            HttpServletRequest httpRequest) {
        String memberUsername = getSessionUsername(httpRequest);
        var schedule = service.create(request, memberUsername);
        return ResponseEntity.ok(Map.of("id", schedule.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        String username = getSessionUsername(httpRequest);
        if (!isAdmin(httpRequest) && !service.isOwner(id, username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
