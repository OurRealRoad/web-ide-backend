package com.jinro.webide.controller;

import com.jcraft.jsch.JSchException;
import com.jinro.webide.domain.Project;
import com.jinro.webide.dto.ProjectRequestDTO;
import com.jinro.webide.service.JSchService;
import com.jinro.webide.service.ProjectService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final JSchService jSchService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getProjectLIst(@PathVariable String memberId) {
        return new ResponseEntity<>(projectService.getAllProject(memberId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createProject(@RequestBody ProjectRequestDTO requestDTO) {
        try {
            Project project = projectService.createProject(requestDTO);

            return new ResponseEntity<>(project, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error occurred while creating project: {}", e.getMessage(), e);

            return new ResponseEntity<>("서버에서 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{projectId}/run")
    public ResponseEntity<?> runProject(@PathVariable String projectId) throws JSchException, IOException {
        projectService.runProject(projectId);
        jSchService.createSession(projectId, projectService.getProjectContainerPort(projectId));
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    @PostMapping("/{projectId}/stop")
    public ResponseEntity<?> stopProject(@PathVariable String projectId) {
        projectService.stopProject(projectId);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<?> deleteProject(@PathVariable String projectId) {
        projectService.deleteProject(projectId);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }
}