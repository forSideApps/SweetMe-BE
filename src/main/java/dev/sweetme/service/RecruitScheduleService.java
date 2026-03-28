package dev.sweetme.service;

import dev.sweetme.domain.RecruitSchedule;
import dev.sweetme.dto.RecruitScheduleRequest;
import dev.sweetme.repository.RecruitScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitScheduleService {

    private final RecruitScheduleRepository repository;

    public List<RecruitSchedule> findAll(boolean upcomingOnly) {
        if (upcomingOnly) {
            return repository.findByAnnounceDateGreaterThanEqualOrderByAnnounceDateAscCreatedAtDesc(
                    LocalDate.now()
            );
        }
        return repository.findAllByOrderByAnnounceDateAscCreatedAtDesc();
    }

    public RecruitSchedule findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));
    }

    @Transactional
    public RecruitSchedule create(RecruitScheduleRequest req, String memberUsername) {
        String displayName = memberUsername != null ? memberUsername : req.getSubmitterName();
        return repository.save(RecruitSchedule.builder()
                .company(req.getCompany().trim())
                .hireType(req.getHireType())
                .stage(req.getStage())
                .announceDate(req.getAnnounceDate())
                .announceTime(req.getAnnounceTime())
                .notes(req.getNotes())
                .submittedBy(memberUsername)
                .submitterName(displayName)
                .build());
    }

    public boolean isOwner(Long id, String username) {
        if (username == null) return false;
        return username.equals(findById(id).getSubmittedBy());
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(findById(id));
    }
}
