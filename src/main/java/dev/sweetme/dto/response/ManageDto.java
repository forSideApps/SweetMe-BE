package dev.sweetme.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ManageDto {

    private RoomDetailDto room;
    private List<ApplicationDto> applications;
}
