package com.project_management.service;

import com.project_management.response.DashboardResponse;

import java.util.UUID;

public interface DashboardService {

    DashboardResponse getDashboard(UUID organizationId);

}