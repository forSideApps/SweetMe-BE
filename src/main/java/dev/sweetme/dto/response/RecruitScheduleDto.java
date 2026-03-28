package dev.sweetme.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.sweetme.domain.RecruitSchedule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RecruitScheduleDto {

    private Long id;
    private String company;
    private String hireType;
    private String stage;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate announceDate;

    private String announceTime;
    private String notes;
    private String submittedBy;
    private String submitterName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public static RecruitScheduleDto from(RecruitSchedule s) {
        return new RecruitScheduleDto(
                s.getId(),
                s.getCompany(),
                s.getHireType(),
                s.getStage(),
                s.getAnnounceDate(),
                s.getAnnounceTime(),
                s.getNotes(),
                s.getSubmittedBy(),
                s.getSubmitterName(),
                s.getCreatedAt()
        );
    }
}
