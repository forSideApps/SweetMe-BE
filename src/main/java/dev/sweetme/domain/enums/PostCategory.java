package dev.sweetme.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostCategory {
    NOTICE("공지사항"),
    SUGGESTION("건의 / 기능 요청"),
    FREE("자유게시판"),
    COMPANY_SCHEDULE("채용 일정 정보");

    private final String displayName;
}
