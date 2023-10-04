package com.jinro.webide.service;

import java.util.UUID;

public interface DockerService {

    int run(UUID projectId);

    void delete(UUID projectId);

    void createContainer(UUID projectId);

    void startContainer(UUID projectId);

    void pauseContainer(UUID projectId);

    void unpauseContainer(UUID projectId);

    void stopContainer(UUID projectId);

    void removeContainer(UUID projectId);
}