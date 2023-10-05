package com.jinro.webide.repository;

import com.jinro.webide.domain.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository {

    Project save(Project project);

    void lastUsingTimeUpdate(String projectId);

    Optional<Project> findById(String projectId);

    List<Project> findAll(String memberId);

    void deleteById(String projectId);

    void delete(String memberId);
}