package com.project_management.service;

import com.project_management.request.ProjectRequest;
import com.project_management.response.ProjectResponse;

import java.util.List;
import java.util.UUID;

public interface ProjectService {

    ProjectResponse createProject(ProjectRequest request);

    ProjectResponse updateProject(UUID id,
                                  ProjectRequest request);

    ProjectResponse getProject(UUID id);

    List<ProjectResponse> getProjects(UUID organizationId);

    void deleteProject(UUID id);
}
