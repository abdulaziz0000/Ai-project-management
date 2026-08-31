package com.project_management.repository;

import com.project_management.entity.Project;
import com.project_management.enums.ProjectStatus;
import com.project_management.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    List<Project> findByOrganizationId(UUID organizationId);

    long countByOrganization_Id(UUID organizationId);
    long countByOrganization_IdAndStatus(UUID organizationId, ProjectStatus status);



}
