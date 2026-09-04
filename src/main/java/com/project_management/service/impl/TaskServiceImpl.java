package com.project_management.service.impl;

import com.project_management.entity.Project;
import com.project_management.entity.Task;
import com.project_management.entity.User;
import com.project_management.enums.TaskStatus;
import com.project_management.ingestion.WorkspaceKnowledgeIndexer;
import com.project_management.repository.ProjectRepository;
import com.project_management.repository.TaskRepository;
import com.project_management.repository.UserRepository;
import com.project_management.request.TaskRequest;
import com.project_management.response.TaskResponse;
import com.project_management.service.EmailService;
import com.project_management.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final WorkspaceKnowledgeIndexer workspaceKnowledgeIndexer;
    private final EmailService emailService;


    @Override
    public TaskResponse createTask(TaskRequest request) {

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User user = userRepository.findById(request.getAssignedTo())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = modelMapper.map(request, Task.class);

        task.setTitle(request.getTitle());
        task.setProject(project);
        task.setAssignedTo(user);

        Task savedTask = taskRepository.save(task);


// Send assignment email
        String employeeName =
                user.getFirstName() + " " + user.getLastName();

        String subject =
                "You have been assigned a new task";

        String message =
                "Hello " + employeeName + ",\n\n" +
                        "You have been assigned a new task.\n\n" +
                        "Task: " + savedTask.getTitle() + "\n" +
                        "Project: " + project.getName() + "\n" +
                        "Priority: " + savedTask.getPriority() + "\n" +
                        "Status: " + savedTask.getStatus() + "\n" +
                        "Due Date: " + savedTask.getDueDate() + "\n\n" +
                        "Please log in to the Project Management System " +
                        "to view the task and its details.\n\n" +
                        "Regards,\n" +
                        "Project Management System";

        emailService.sendEmail(
                user.getEmail(),
                subject,
                message
        );

        workspaceKnowledgeIndexer.indexTask(
                project.getOrganization().getId(),
                project.getId(),
                savedTask.getId(),
                project.getName(),
                savedTask.getTitle(),
                savedTask.getDescription(),
                savedTask.getStatus().name(),
                savedTask.getPriority().name(),
                user.getFirstName() + " " + user.getLastName(),
                user.getFirstName() + " " + user.getLastName()
        );

        return modelMapper.map(savedTask, TaskResponse.class);
    }

    @Override
    public TaskResponse updateTask(UUID id, TaskRequest request) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User user = userRepository.findById(request.getAssignedTo())
                .orElseThrow(() -> new RuntimeException("User not found"));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setPriority(request.getPriority());
        task.setStatus(request.getStatus());
        task.setProject(project);
        task.setAssignedTo(user);

        Task updatedTask = taskRepository.save(task);

        workspaceKnowledgeIndexer.indexTask(
                project.getOrganization().getId(),
                project.getId(),
                updatedTask.getId(),
                project.getName(),
                updatedTask.getTitle(),
                updatedTask.getDescription(),
                updatedTask.getStatus().name(),
                updatedTask.getPriority().name(),
                user.getFirstName() + " " + user.getLastName(),
                user.getFirstName() + " " + user.getLastName()
        );

        return modelMapper.map(updatedTask, TaskResponse.class);
    }

    @Override
    public void assignTask(UUID taskId, UUID userId) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        task.setAssignedTo(user);

        Task savedTask = taskRepository.save(task);


        // ==========================================
        // SEND ASSIGNMENT EMAIL
        // ==========================================

        String employeeName =
                user.getFirstName() + " " + user.getLastName();

        String projectName =
                task.getProject() != null
                        ? task.getProject().getName()
                        : "Project";

        String subject =
                "You have been assigned a new task";

        String message =
                "Hello " + employeeName + ",\n\n" +

                        "You have been assigned a new task.\n\n" +

                        "Task: " + savedTask.getTitle() + "\n" +
                        "Project: " + projectName + "\n" +
                        "Priority: " + savedTask.getPriority() + "\n" +
                        "Status: " + savedTask.getStatus() + "\n" +
                        "Due Date: " + savedTask.getDueDate() + "\n\n" +

                        "Please log in to the Project Management System " +
                        "to view the task and its details.\n\n" +

                        "Regards,\n" +
                        "Project Management System";

        emailService.sendEmail(
                user.getEmail(),
                subject,
                message
        );
    }


    @Override
    public void updateStatus(UUID taskId, TaskStatus status) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setStatus(status);

        taskRepository.save(task);
    }


    @Override
    public TaskResponse getTaskById(UUID id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        return mapToResponse(task);
    }


    @Override
    public List<TaskResponse> getTasksByProject(UUID projectId) {

        return taskRepository.findByProjectId(projectId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    @Override
    public List<TaskResponse> getAssignedTasks(UUID userId) {

        return taskRepository.findByAssignedToId(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    @Override
    public void deleteTask(UUID uuid) {

        Task task = taskRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("task not found"));

        taskRepository.delete(task);
    }


    private TaskResponse mapToResponse(Task task) {

        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .priority(task.getPriority())
                .status(task.getStatus())

                .projectId(task.getProject().getId())
                .projectName(task.getProject().getName())

                .createdBy(
                        task.getCreatedBy() != null
                                ? task.getCreatedBy().getId()
                                : null
                )

                .createdByName(
                        task.getCreatedBy() != null
                                ? task.getCreatedBy().getFirstName()
                                  + " "
                                  + task.getCreatedBy().getLastName()
                                : "Unknown"
                )

                .assignedTo(
                        task.getAssignedTo() != null
                                ? task.getAssignedTo().getId()
                                : null
                )

                .assignedToName(
                        task.getAssignedTo() != null
                                ? task.getAssignedTo().getFirstName()
                                  + " "
                                  + task.getAssignedTo().getLastName()
                                : "Unassigned"
                )

                .build();
    }
}