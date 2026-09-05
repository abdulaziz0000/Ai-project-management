package com.project_management.service.impl;

import com.project_management.entity.Organization;
import com.project_management.entity.Project;
import com.project_management.entity.ProjectMember;
import com.project_management.entity.User;
import com.project_management.enums.ProjectRole;
import com.project_management.enums.Role;
import com.project_management.ingestion.WorkspaceKnowledgeIndexer;
import com.project_management.repository.OrganizationRepository;
import com.project_management.repository.ProjectMemberRepository;
import com.project_management.repository.ProjectRepository;
import com.project_management.repository.UserRepository;
import com.project_management.request.ProjectRequest;
import com.project_management.response.ProjectResponse;
import com.project_management.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.project_management.repository.TaskRepository;
import com.project_management.repository.InvitationRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final ProjectMemberRepository projectMemberRepository;
    private final WorkspaceKnowledgeIndexer workspaceKnowledgeIndexer; // add this
    private final TaskRepository taskRepository;
    private final InvitationRepository invitationRepository;


    @Override
    public ProjectResponse createProject(ProjectRequest request) {

        Organization organization = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        User createdBy = userRepository.findById(request.getCreatedBy())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = new Project();

        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        project.setStatus(request.getStatus());
        project.setPriority(request.getPriority());

        project.setOrganization(organization);
        project.setCreatedBy(createdBy);

        Project savedProject = projectRepository.save(project);
        ProjectMember member = new ProjectMember();

        member.setProject(savedProject);
        member.setUser(createdBy);
        member.setRole(Role.SUPER_ADMIN);   // or ADMIN
        member.setActive(true);

        projectMemberRepository.save(member);

        workspaceKnowledgeIndexer.indexProject(
                organization.getId(),
                savedProject.getId(),
                savedProject.getName(),
                savedProject.getDescription(),
                savedProject.getStatus() != null ? savedProject.getStatus().toString() : "Not specified",
                savedProject.getPriority() != null ? savedProject.getPriority().toString() : "Not specified",
                savedProject.getStartDate() != null ? savedProject.getStartDate().toString() : "Not specified",
                savedProject.getEndDate() != null ? savedProject.getEndDate().toString() : "Not specified",
                createdBy.getFirstName() + " " + createdBy.getLastName()
        );
        ProjectResponse response = new ProjectResponse();

        response.setId(savedProject.getId());
        response.setName(savedProject.getName());
        response.setDescription(savedProject.getDescription());
        response.setStartDate(savedProject.getStartDate());
        response.setEndDate(savedProject.getEndDate());
        response.setStatus(savedProject.getStatus());
        response.setPriority(savedProject.getPriority());

        response.setOrganizationId(organization.getId());
        response.setOrganizationName(organization.getName());

        response.setCreatedBy(createdBy.getId());
        response.setCreatedByName(createdBy.getFirstName() + " " + createdBy.getLastName());

        return response;
    }

    @Override
    public ProjectResponse updateProject(UUID id, ProjectRequest request) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Organization organization = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        User createdBy = userRepository.findById(request.getCreatedBy())
                .orElseThrow(() -> new RuntimeException("User not found"));

        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        project.setStatus(request.getStatus());
        project.setPriority(request.getPriority());
        project.setOrganization(organization);
        project.setCreatedBy(createdBy);

        Project updatedProject = projectRepository.save(project);
        System.out.println("Project ID = " + project.getId());
        System.out.println("Organization ID = " + organization.getId());
        workspaceKnowledgeIndexer.indexProject(
                organization.getId(),
                updatedProject.getId(),
                updatedProject.getName(),
                updatedProject.getDescription(),
                updatedProject.getStatus() != null ? updatedProject.getStatus().toString() : "Not specified",
                updatedProject.getPriority() != null ? updatedProject.getPriority().toString() : "Not specified",
                updatedProject.getStartDate() != null ? updatedProject.getStartDate().toString() : "Not specified",
                updatedProject.getEndDate() != null ? updatedProject.getEndDate().toString() : "Not specified",
                createdBy.getFirstName() + " " + createdBy.getLastName()
        );

        return modelMapper.map(updatedProject, ProjectResponse.class);
    }

    public ProjectResponse getProject(UUID id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        ProjectResponse response = modelMapper.map(project, ProjectResponse.class);

        if (project.getOrganization() != null) {
            response.setOrganizationId(project.getOrganization().getId());
            response.setOrganizationName(project.getOrganization().getName());
        }

        if (project.getCreatedBy() != null) {
            response.setCreatedBy(project.getCreatedBy().getId());
            response.setCreatedByName(
                    project.getCreatedBy().getFirstName() + " " + project.getCreatedBy().getLastName()
            );
        }

        return response;
    }
    @Override
    public List<ProjectResponse> getProjects(UUID organizationId) {
        List<Project> projects = projectRepository.findByOrganizationId(organizationId); // whatever your query is
        return projects.stream()
                .map(project -> {

                    ProjectResponse response = new ProjectResponse();

                    response.setId(project.getId());
                    response.setName(project.getName());
                    response.setDescription(project.getDescription());
                    response.setStartDate(project.getStartDate());
                    response.setEndDate(project.getEndDate());
                    response.setStatus(project.getStatus());
                    response.setPriority(project.getPriority());

                    if (project.getOrganization() != null) {
                        response.setOrganizationId(project.getOrganization().getId());
                        response.setOrganizationName(project.getOrganization().getName());
                    }

                    if (project.getCreatedBy() != null) {
                        response.setCreatedBy(project.getCreatedBy().getId());
                        response.setCreatedByName(
                                project.getCreatedBy().getFirstName() + " " +
                                        project.getCreatedBy().getLastName()
                        );
                    }

                    return response;
                })
                .toList();
    }
    @Override
    @Transactional
    public void deleteProject(UUID id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Delete child records first
        invitationRepository.deleteByProjectId(id);
        taskRepository.deleteByProjectId(id);
        projectMemberRepository.deleteByProjectId(id);

        // Delete project
        projectRepository.delete(project);
    }
}