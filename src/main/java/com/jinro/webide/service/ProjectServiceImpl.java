package com.jinro.webide.service;

import com.jinro.webide.domain.FileNode;
import com.jinro.webide.domain.Project;
import com.jinro.webide.dto.ProjectRequestDTO;
import com.jinro.webide.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final DockerService dockerService;
    private final ProjectRepository projectRepository;

    @Override
    public Project createProject(ProjectRequestDTO requestDTO) {

        Project project = new Project();
        project.setMemberId(requestDTO.getMemberId());
        project.setProjectName(requestDTO.getProjectName());
        project.setProjectLang(requestDTO.getProjectLang());

        projectRepository.save(project);

        return project;
    }

    @Override
    public List<Project> getAllProject(String memberId) {
        return projectRepository.findAll(memberId);
    }

    @Override
    public void runProject(String projectId) {
        // ToDo : pause 상태로 있으면 unpause로 돌리는거 추후 추가
        dockerService.run(projectId);
        projectRepository.lastUsingTimeUpdate(projectId);
    }

    @Override
    public void stopProject(String projectId) {
        // ToDo : pause로 두고 일정시간이 지나면 컨테이너를 삭제하는 방식으로 추후 변경
        dockerService.delete(projectId);
        projectRepository.lastUsingTimeUpdate(projectId);
    }

    @Override
    public void deleteProject(String projectId) {
        dockerService.delete(projectId);
        projectRepository.deleteById(projectId);
    }

    @Override
    public int getProjectContainerPort(String projectId) {
        return dockerService.getContainerPort(projectId);
    }

    @Override
    public FileNode getDirectoryScanList(String projectId) {
        final String PATH = "/Users/jeosong/data/" + projectId + "/workspace/";
        File root = new File(PATH);
        FileNode rootNode = new FileNode();

        rootNode.setName(root.getName());
        rootNode.set_id(0);
        rootNode.setChildren(traverse(root, new AtomicInteger(1)));
        return rootNode;
    }

    private List<FileNode> traverse(File dir, AtomicInteger idCounter) {
        List<FileNode> children = new ArrayList<>();
        File[] files = dir.listFiles();

        if (files != null) {
            Arrays.sort(files, Comparator.comparing(File::getName));
            for (File file : files) {
                FileNode node = new FileNode();

                node.setName(file.getName());
                node.set_id(idCounter.getAndIncrement());
                if (file.isDirectory())
                    node.setChildren(traverse(file, idCounter));
                children.add(node);
            }
        }
        return children;
    }
}