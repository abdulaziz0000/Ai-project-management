package com.project_management.repository;

import com.project_management.entity.User;
import com.project_management.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    List<User> findByOrganizationId(UUID organizationId);

    boolean existsByEmail(String email);
    long countByOrganizationId(UUID organizationId);

    long countByOrganizationIdAndStatus(
            UUID organizationId,
            TaskStatus status
    );
}
