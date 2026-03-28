package dev.sweetme.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RecruitScheduleRequest {

    @NotBlank(message = "기업명을 입력해주세요.")
    @Size(max = 100)
    private String company;

    @NotBlank(message = "채용 유형을 선택해주세요.")
    @Size(max = 50)
    private String hireType;

    @NotBlank(message = "전형 단계를 선택해주세요.")
    @Size(max = 50)
    private String stage;

    @NotNull(message = "결과 발표 날짜를 입력해주세요.")
    private LocalDate announceDate;

    @Size(max = 5)
    private String announceTime;

    @Size(max = 1000)
    private String notes;

    @NotBlank(message = "작성자명을 입력해주세요.")
    @Size(max = 50)
    private String submitterName;
}
