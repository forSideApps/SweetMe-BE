package dev.sweetme.dto;

import dev.sweetme.domain.enums.JobRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomCreateRequest {

    @NotBlank(message = "방 제목을 입력해주세요.")
    @Size(max = 100, message = "제목은 100자 이내로 입력해주세요.")
    private String title;

    @Size(max = 2000, message = "설명은 2000자 이내로 입력해주세요.")
    private String description;

    @Size(max = 50, message = "닉네임은 50자 이내로 입력해주세요.")
    private String creatorNickname;

    @Size(min = 4, max = 20, message = "비밀번호는 4~20자로 입력해주세요.")
    private String password;

    @NotBlank(message = "카카오 오픈채팅 링크를 입력해주세요.")
    @Size(max = 500, message = "카카오 오픈채팅 링크는 500자 이내로 입력해주세요.")
    private String kakaoLink;

    @NotNull(message = "직군을 선택해주세요.")
    private JobRole jobRole;
}
