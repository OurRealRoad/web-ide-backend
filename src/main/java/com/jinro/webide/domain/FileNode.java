package com.jinro.webide.domain;

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
    private boolean isOpen = false;
}