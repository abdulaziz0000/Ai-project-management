package com.project_management.repository;

import com.project_management.entity.Invitation;
import com.project_management.enums.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InvitationRepository extends JpaRepository<Invitation, UUID> {

    Optional<Invitation> findByToken(String token);

    Optional<Invitation> findByEmailAndProjectId(
            String email,
            UUID projectId
    );

    Optional<Invitation> findByEmailAndStatus(
            String email,
            InvitationStatus status
    );

    void deleteByProjectId(UUID projectId);
}
