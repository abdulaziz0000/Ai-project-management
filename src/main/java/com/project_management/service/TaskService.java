package com.project_management.service;

import com.project_management.enums.TaskStatus;
import com.project_management.request.TaskRequest;
import com.project_management.response.TaskResponse;

import java.util.List;
import java.util.UUID;

public interface TaskService {

    TaskResponse createTask(TaskRequest request);

    TaskResponse updateTask(UUID id,
                            TaskRequest request);

    void assignTask(UUID taskId,
                    UUID userId);

    void updateStatus(UUID taskId,
                      TaskStatus status);
    TaskResponse getTaskById(UUID id);

    List<TaskResponse> getTasksByProject(UUID projectId);

    List<TaskResponse> getAssignedTasks(UUID userId);

    void deleteTask(UUID uuid);
}