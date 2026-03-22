package dev.sweetme.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequest {
    private String type;
    private String jobCategory;
    private String careerLevel;
    private String title;
    private String content;
    private String authorName;
    private String contactInfo;
    @Size(max = 100, message = "비밀번호는 100자 이내로 입력해주세요.")
    private String password;
    private String portfolioLink;
}
