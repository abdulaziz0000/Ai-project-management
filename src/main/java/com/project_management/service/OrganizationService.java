package com.project_management.service;

import com.project_management.request.OrganizationRequest;
import com.project_management.response.OrganizationResponse;

import java.util.List;
import java.util.UUID;

public interface OrganizationService {

    OrganizationResponse createOrganization(OrganizationRequest request);

    OrganizationResponse getOrganization(UUID id);

    List<OrganizationResponse> getAllOrganizations();

    OrganizationResponse updateOrganization(UUID id,
                                            OrganizationRequest request);

    void deleteOrganization(UUID id);
}
