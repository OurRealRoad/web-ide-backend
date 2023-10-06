package com.jinro.webide.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FileSystemDTO {

    @NotBlank(message = "name이 null 일 순 없습니다.")
    String path;

    String content;

    @NotBlank(message = "isDirectory가 null 일 순 없습니다.")
    boolean isDirectory;
}