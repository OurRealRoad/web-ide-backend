package com.jinro.webide.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class ProjectRequestDTO {

    @NotBlank(message = "member id가 null 일 순 없습니다.")
    private UUID memberId;

    @NotBlank
    @Size(min = 4, max = 20, message = "프로젝트 이름은 4 ~ 20자 이여야 합니다.")
    private String projectName;

    @NotBlank
    @Size(min = 1, max = 10, message = "프로젝트 언어는 1 ~ 10자 이여야 합니다.")
    private String projectLang;
}