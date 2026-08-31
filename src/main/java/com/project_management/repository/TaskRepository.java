package com.project_management.repository;

import com.project_management.entity.Task;
import com.project_management.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByProjectId(UUID projectId);

    List<Task> findByAssignedToId(UUID userId);

    long countByProjectOrganizationIdAndStatus(
            UUID organizationId,
            TaskStatus status
    );

}
