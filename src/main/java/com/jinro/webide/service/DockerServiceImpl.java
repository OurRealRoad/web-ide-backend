package com.jinro.webide.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.*;
import com.jinro.webide.constants.InfraConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class DockerServiceImpl implements DockerService {

    private final DockerClient dockerClient;
    // Map<ProjectId, ContainerId>
    private final Map<String, String> projectMap = new ConcurrentHashMap<>();

    /**
     * 프로젝트 시작시 컨테이너를 생성 및 실행
     *
     * @param projectId 프로젝트는 하나의 컨테이너만을 갖기 위해 만들것이기 때문에 프로젝트 id
     * @return jsch(ssh)에 필요한 port 반환
     */
    @Override
    public int run(String projectId) {
        createContainer(projectId);
        startContainer(projectId);

        return getContainerPort(projectId);
    }

    @Override
    public void delete(String projectId) {
        stopContainer(projectId);
        removeContainer(projectId);
    }

    /**
     * 컨테이너 생성
     *
     * @param projectId 프로젝트와 컨테이너는 1:1 대응
     * @return container id
     */
    @Override
    public void createContainer(String projectId) {
        if (projectMap.containsKey(projectId))
            throw new IllegalArgumentException("Container is already allocated to the project.");
        CreateContainerResponse container = dockerClient.createContainerCmd(InfraConst.IMAGE_ID)
                .withHostConfig(getDockerConfig(projectId))
                .exec();

        String containerId = container.getId();

        projectMap.put(projectId, containerId);
    }

    /**
     * 컨테이너 시작
     *
     * @param projectId Map에서 꺼내기 위한 key
     * @return port 반환
     */
    @Override
    public void startContainer(String projectId) {
        String containerId = getContainerId(projectId);

        dockerClient.startContainerCmd(containerId).exec();
    }

    @Override
    public void pauseContainer(String projectId) {
        String containerId = getContainerId(projectId);

        dockerClient.pauseContainerCmd(containerId).exec();
    }

    @Override
    public void unpauseContainer(String projectId) {
        String containerId = getContainerId(projectId);

        dockerClient.unpauseContainerCmd(containerId).exec();
    }

    @Override
    public void stopContainer(String projectId) {
        String containerId = getContainerId(projectId);

        dockerClient.stopContainerCmd(containerId).exec();
    }

    @Override
    public void removeContainer(String projectId) {
        String containerId = getContainerId(projectId);

        dockerClient.removeContainerCmd(containerId).exec();
        projectMap.remove(projectId);
    }

    private String getContainerId(String projectId) {
        String containerId = projectMap.get(projectId);

        if (containerId == null)
            throw new IllegalStateException("Container is not allocated to the project.");

        return containerId;
    }

    /**
     * 컨테이너 시작시 랜덤 포트 할당으로 인한
     * port 받아오기
     *
     * @param projectId Map에서 꺼내기 위한 key
     * @return port
     */
    @Override
    public int getContainerPort(String projectId) {
        String containerId = getContainerId(projectId);
        ExposedPort tcp22 = ExposedPort.tcp(InfraConst.EXPOSED_PORT);
        InspectContainerResponse containerInfo = dockerClient.inspectContainerCmd(containerId).exec();

        Ports ports = containerInfo.getNetworkSettings().getPorts();

        return Integer.parseInt(ports.getBindings().get(tcp22)[0].getHostPortSpec());
    }

    /**
     * 프로젝트 시작시 컨테이너를 생성할건데,
     * 생성 할 때 필요한 각종 옵션 설정
     *
     * @param projectId 프로젝트 시작시 컨테이너를 생성할것이기 때문에 프로젝트 id
     * @return
     */
    private HostConfig getDockerConfig(String projectId) {
        String userHome = System.getProperty("user.home");

        // Docker Port Forwarding
        final ExposedPort tcp22 = ExposedPort.tcp(InfraConst.EXPOSED_PORT);
        final Ports portBindings = new Ports(tcp22, Ports.Binding.bindPort(0));

        // Docker Volume Mount
        final String hostPath = userHome + "/MountData/" + projectId + "/workspace/";
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