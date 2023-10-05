package com.jinro.webide.service;

public interface DockerService {

    int run(String projectId);

    void delete(String projectId);

    void createContainer(String projectId);

    void startContainer(String projectId);

    void pauseContainer(String projectId);

    void unpauseContainer(String projectId);

    void stopContainer(String projectId);

    void removeContainer(String projectId);

    int getContainerPort(String projectId);
}