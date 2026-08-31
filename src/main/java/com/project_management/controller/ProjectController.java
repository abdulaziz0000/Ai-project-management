package com.project_management.controller;

import com.project_management.request.ProjectRequest;
import com.project_management.response.ProjectResponse;
import com.project_management.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping
    public ProjectResponse createProject(@RequestBody ProjectRequest request) {
        System.out.println("inside contoller");
        return projectService.createProject(request);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}")
    public ProjectResponse updateProject(@PathVariable UUID id,
                                         @RequestBody ProjectRequest request) {
        return projectService.updateProject(id, request);
    }

    @GetMapping("/{id}")
    public ProjectResponse getProject(@PathVariable UUID id) {
        return projectService.getProject(id);
    }

    @GetMapping("/organization/{organizationId}")
    public List<ProjectResponse> getProjects(@PathVariable UUID organizationId) {
        return projectService.getProjects(organizationId);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable UUID id) {
        projectService.deleteProject(id);
    }
}