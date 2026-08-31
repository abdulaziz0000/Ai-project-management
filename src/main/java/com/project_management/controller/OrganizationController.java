package com.project_management.controller;
import com.project_management.request.OrganizationRequest;
import com.project_management.response.OrganizationResponse;
import com.project_management.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    public OrganizationResponse createOrganization(@RequestBody OrganizationRequest request) {
        return organizationService.createOrganization(request);
    }

    @GetMapping("/{id}")
    public OrganizationResponse getOrganization(@PathVariable UUID id) {
        return organizationService.getOrganization(id);
    }

    @GetMapping
    public List<OrganizationResponse> getAllOrganizations() {
        return organizationService.getAllOrganizations();
    }

    @PutMapping("/{id}")
    public OrganizationResponse updateOrganization(@PathVariable UUID id,
                                                   @RequestBody OrganizationRequest request) {
        return organizationService.updateOrganization(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteOrganization(@PathVariable UUID id) {
        organizationService.deleteOrganization(id);
    }
}