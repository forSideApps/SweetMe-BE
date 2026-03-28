package dev.sweetme.dto;

import dev.sweetme.domain.enums.JobRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomUpdateRequest {

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 100, message = "제목은 100자 이내로 입력해주세요.")
    private String title;

    @Size(max = 2000)
    private String description;

    @Size(max = 500)
    private String kakaoLink;

    private JobRole jobRole;
}
