package com.jinro.webide.service;

public interface DockerService {
    public String createContainer(String projectId);

    public int startContainer(String containerId);

    public void pauseContainer(String containerId);

    public void unpauseContainer(String containerId);

    public void stopContainer(String containerId);

    public void removeContainer(String containerId);
}
