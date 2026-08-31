package com.project_management.request;
import com.project_management.enums.Priority;
import com.project_management.enums.TaskStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class TaskRequest {

    private String title;
    private String description;
    private LocalDate dueDate;
    private Priority priority;
    private TaskStatus status;
    private UUID projectId;
    private UUID assignedTo;
}