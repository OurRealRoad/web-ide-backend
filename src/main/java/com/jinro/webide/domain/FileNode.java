package com.jinro.webide.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FileNode {
    private String name;
    private List<FileNode> children;
    private int _id;
    private int checked = 0;

    @JsonProperty("isOpen") // 명시적으로 JSON 키 이름 지정
    private boolean isOpen = false;
}