package com.project_management.response;

import com.project_management.enums.Priority;
import com.project_management.enums.ProjectStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class ProjectResponse {

    private UUID id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private ProjectStatus status;
    private Priority priority;
    private UUID organizationId;
    private String organizationName;
    private UUID createdBy;
    private String createdByName;
}