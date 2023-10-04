package com.jinro.webide.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    private UUID projectId;
    private UUID memberId;
    private String projectName;
    private String projectLang;
    private LocalDateTime regDate;
    private LocalDateTime chgDate;

}