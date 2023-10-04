package com.jinro.webide.repository;

import com.jinro.webide.domain.Project;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository {

    Project save(Project project);

    void lastUsingTimeUpdate(UUID projectId);

    Optional<Project> findById(UUID projectId);

    List<Project> findAll(UUID memberId);

    void deleteById(UUID projectId);

    void delete(UUID memberId);
}