package com.jinro.webide.service;

import com.jinro.webide.domain.Project;
import com.jinro.webide.dto.ProjectRequestDTO;

import java.util.List;
import java.util.UUID;

public interface ProjectService {
    Project createProject(ProjectRequestDTO requsetDTO);

    List<Project> getAllProject(UUID memberId);

    void runProject(UUID projectId);

    void stopProject(UUID projectId);

    void deleteProject(UUID projectId);
}