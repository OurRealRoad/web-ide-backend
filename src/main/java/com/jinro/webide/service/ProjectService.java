package com.jinro.webide.service;

import com.jinro.webide.domain.Project;
import com.jinro.webide.dto.ProjectRequestDTO;

import java.util.List;

public interface ProjectService {
    Project createProject(ProjectRequestDTO requsetDTO);

    List<Project> getAllProject(String memberId);

    void runProject(String projectId);

    void stopProject(String projectId);

    void deleteProject(String projectId);

    int getProjectContainerPort(String projectId);
}