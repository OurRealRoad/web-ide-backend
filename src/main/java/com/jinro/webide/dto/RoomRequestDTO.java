package com.jinro.webide.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequestDTO {
    private String projectId; //프로젝트 고유키
    private String roomId; //방 고유키
    private String roomName; //방이름
}
