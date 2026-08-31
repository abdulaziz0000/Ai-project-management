package com.project_management.service.impl;

import com.project_management.entity.Organization;
import com.project_management.entity.User;
import com.project_management.enums.ProjectStatus;
import com.project_management.enums.TaskStatus;
import com.project_management.repository.OrganizationRepository;
import com.project_management.repository.ProjectRepository;
import com.project_management.repository.TaskRepository;
import com.project_management.repository.UserRepository;
import com.project_management.response.DashboardResponse;
import com.project_management.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    @Override
    public DashboardResponse getDashboard(UUID organizationId) {

        Organization organization =
                organizationRepository.findById(organizationId)
                        .orElseThrow(() -> new RuntimeException("Organization not found"));

        User manager = userRepository
                .findById(organization.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        long totalProjects =
                projectRepository.countByOrganization_Id(organizationId);
        long totalUsers =
                userRepository.countByOrganizationId(organizationId);

        long openTasks =
                taskRepository.countByProjectOrganizationIdAndStatus(
                        organizationId,
                        TaskStatus.IN_PROGRESS);

        long completedTasks =
                taskRepository.countByProjectOrganizationIdAndStatus(
                        organizationId,
                        TaskStatus.COMPLETED);

        return DashboardResponse.builder()
                .firstName(manager.getFirstName())
                .organizationName(organization.getName())
                .totalProjects(totalProjects)
                .totalUsers(totalUsers)
                .openTasks(openTasks)
                .completedTasks(completedTasks)
                .build();

    }
}
