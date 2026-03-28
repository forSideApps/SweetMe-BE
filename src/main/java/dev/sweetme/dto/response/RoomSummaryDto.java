package dev.sweetme.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.sweetme.domain.Room;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoomSummaryDto {

    private Long id;
    private String title;
    private String status;
    private String statusDisplay;
    private String creatorNickname;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private String themeName;
    private Long themeId;
    private String themeSlug;
    private String themeLogoUrl;
    private String jobRole;
    private String jobRoleDisplay;

    public static RoomSummaryDto from(Room room, String logoBaseUrl) {
        return new RoomSummaryDto(
                room.getId(),
                room.getTitle(),
                room.getStatus().name(),
                room.getStatus().getDisplayName(),
                room.getCreatorNickname(),
                room.getCreatedAt(),
                room.getCompany().getName(),
                room.getCompany().getId(),
                room.getCompany().getSlug(),
                logoBaseUrl + room.getCompany().getSlug() + ".png",
                room.getJobRole() != null ? room.getJobRole().name() : null,
                room.getJobRole() != null ? room.getJobRole().getDisplayName() : null
        );
    }
}
