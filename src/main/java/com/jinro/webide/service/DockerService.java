package com.jinro.webide.service;

public interface DockerService {
    public void createContainer(String projectId);

    public void startContainer(String projectId);

    public void pauseContainer(String projectId);

    public void unpauseContainer(String projectId);

    public void stopContainer(String projectId);

    public void removeContainer(String projectId);
}
