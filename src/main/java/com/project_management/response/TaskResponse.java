package com.project_management.response;


import com.project_management.enums.Priority;
import com.project_management.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskResponse {

    private UUID id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private Priority priority;
    private TaskStatus status;

    private UUID projectId;
    private String projectName;

    private UUID createdBy;
    private String createdByName;

    private UUID assignedTo;
    private String assignedToName;
}