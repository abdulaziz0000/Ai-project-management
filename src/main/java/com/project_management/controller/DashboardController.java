package com.project_management.controller;

import com.project_management.response.DashboardResponse;
import com.project_management.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/{organizationId}")
    public DashboardResponse getDashboard(
            @PathVariable UUID organizationId) {

        return dashboardService.getDashboard(organizationId);
    }

}
