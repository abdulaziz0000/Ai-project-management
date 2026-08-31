package com.project_management.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private String firstName;
    private String organizationName;

    private long totalProjects;
    private long totalUsers;
    private long openTasks;
    private long completedTasks;
}