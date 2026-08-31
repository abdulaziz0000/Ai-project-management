package com.project_management.controller;

import com.project_management.enums.TaskStatus;
import com.project_management.request.TaskRequest;
import com.project_management.response.TaskResponse;
import com.project_management.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping
    public TaskResponse createTask(@RequestBody TaskRequest request) {
        return taskService.createTask(request);
    }


    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}")
    public TaskResponse updateTask(@PathVariable UUID id,
                                   @RequestBody TaskRequest request) {
        return taskService.updateTask(id, request);
    }


    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/{taskId}/assign/{userId}")
    public void assignTask(@PathVariable UUID taskId,
                           @PathVariable UUID userId) {
        taskService.assignTask(taskId, userId);
    }

    @PutMapping("/{taskId}/status")
    public void updateStatus(@PathVariable UUID taskId,
                             @RequestParam TaskStatus status) {
        taskService.updateStatus(taskId, status);
    }

    @GetMapping("/{id}")
    public TaskResponse getTaskById(@PathVariable UUID id) {
        return taskService.getTaskById(id);
    }
    @GetMapping("/project/{projectId}")
    public List<TaskResponse> getTasksByProject(@PathVariable UUID projectId) {
        return taskService.getTasksByProject(projectId);
    }

    @GetMapping("/user/{userId}")
    public List<TaskResponse> getAssignedTasks(@PathVariable UUID userId) {
        return taskService.getAssignedTasks(userId);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable UUID id) {
        taskService.deleteTask(id);
    }
}
