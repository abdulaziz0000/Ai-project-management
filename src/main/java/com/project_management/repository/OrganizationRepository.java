package com.project_management.repository;

import com.project_management.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    Optional<Organization> findByEmail(String email);

    boolean existsByName(String name);
}
