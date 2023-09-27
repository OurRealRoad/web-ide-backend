package com.jinro.webide.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ContainerInfoDTO {
    private String containerId;
    private Integer containerPort;
}
