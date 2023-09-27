package com.jinro.webide.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.*;
import com.jinro.webide.constants.InfraConst;
import com.jinro.webide.dto.ContainerInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DockerServiceImpl implements DockerService {

    private final DockerClient dockerClient;

    public ContainerInfoDTO run(String projectId) {
        String containerId = createContainer(projectId);
        int port = startContainer(containerId);
        return new ContainerInfoDTO(containerId, port);
    }

    @Override
    public String createContainer(String projectId) {
        CreateContainerResponse container = dockerClient.createContainerCmd(InfraConst.IMAGE_NAME)
                .withHostConfig(getDockerConfig(projectId))
                .exec();
        return container.getId();
    }

    @Override
    public int startContainer(String containerId) {
        dockerClient.startContainerCmd(containerId).exec();
        return getContainerPort(containerId);
    }

    @Override
    public void pauseContainer(String containerId) {
        dockerClient.pauseContainerCmd(containerId).exec();
    }

    @Override
    public void unpauseContainer(String containerId) {
        dockerClient.unpauseContainerCmd(containerId).exec();
    }

    @Override
    public void stopContainer(String containerId) {
        dockerClient.stopContainerCmd(containerId).exec();
    }

    @Override
    public void removeContainer(String containerId) {
        dockerClient.removeContainerCmd(containerId).exec();
    }

    private int getContainerPort(String containerId) {
        ExposedPort tcp22 = ExposedPort.tcp(InfraConst.EXPOSED_PORT);
        InspectContainerResponse containerInfo = dockerClient.inspectContainerCmd(containerId).exec();

        Ports ports = containerInfo.getNetworkSettings().getPorts();
        return Integer.parseInt(ports.getBindings().get(tcp22)[0].getHostPortSpec());
    }

    private HostConfig getDockerConfig(String projectId) {
        // Docker Port Forwarding
        final ExposedPort tcp22 = ExposedPort.tcp(InfraConst.EXPOSED_PORT);
        final Ports portBindings = new Ports(tcp22, Ports.Binding.bindPort(0));

        // Docker Volume Mount
        final String hostPath = "~/data/" + projectId + "/workspace/";
        final String containerPath = "/workspace";

        Bind bind = new Bind(hostPath, new Volume(containerPath));

        return new HostConfig()
                .withPortBindings(portBindings)
                .withNanoCPUs(InfraConst.LIMIT_CPU) // 0.5 cpu
                .withMemory(InfraConst.LIMIT_MEM) // 512MB
                .withMemorySwap(InfraConst.LIMIT_SWAP) // Swap Memory 1GB
                .withBinds(bind);
    }
}