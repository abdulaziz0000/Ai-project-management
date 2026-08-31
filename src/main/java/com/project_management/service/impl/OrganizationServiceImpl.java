package com.project_management.service.impl;

import com.project_management.entity.Organization;
import com.project_management.entity.Project;
import com.project_management.entity.Task;
import com.project_management.entity.User;
import com.project_management.enums.*;
import com.project_management.ingestion.WorkspaceKnowledgeIndexer;
import com.project_management.repository.OrganizationRepository;
import com.project_management.repository.ProjectRepository;
import com.project_management.repository.TaskRepository;
import com.project_management.repository.UserRepository;
import com.project_management.request.OrganizationRequest;
import com.project_management.response.OrganizationResponse;
import com.project_management.service.OrganizationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final WorkspaceKnowledgeIndexer workspaceKnowledgeIndexer;


    @Override
    @Transactional
    public OrganizationResponse createOrganization(OrganizationRequest request) {

        if (organizationRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Organization email already exists");
        }

        if (userRepository.findByEmail(request.getManagerEmail()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        // ==========================
        // Create Organization
        // ==========================

        Organization organization = new Organization();
        organization.setName(request.getName());
        organization.setIndustry(request.getIndustry());
        organization.setEmail(request.getEmail());
        organization.setPhone(request.getPhone());
        organization.setAddress(request.getAddress());

        Organization savedOrganization = organizationRepository.save(organization);

        // ==========================
        // Create Super Admin
        // ==========================

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getManagerEmail());
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );        user.setPhone(request.getManagerPhone());

        user.setRole(Role.SUPER_ADMIN);
        user.setStatus(UserStatus.ACTIVE);
        user.setEnabled(true);
        user.setOrganization(savedOrganization);

        User savedUser = userRepository.save(user);

        savedOrganization.setOwnerId(savedUser.getId());
        savedOrganization = organizationRepository.save(savedOrganization);

        // ==========================
        // Create Default Project
        // ==========================

        Project project = new Project();
        project.setName("Getting Started");
        project.setDescription("Default project created during organization setup.");
        project.setOrganization(savedOrganization);
        project.setStartDate(LocalDate.now());
        project.setEndDate(LocalDate.now().plusDays(30));
        project.setCreatedBy(savedUser);
        project.setStatus(ProjectStatus.ACTIVE); // if your entity has this field
        project.setPriority(Priority.MEDIUM);

        Project savedProject = projectRepository.save(project);
        workspaceKnowledgeIndexer.indexProject(
                savedOrganization.getId(),
                savedProject.getId(),
                savedProject.getName(),
                savedProject.getDescription(),
                savedProject.getStatus().name(),
                savedProject.getPriority().name(),
                savedProject.getStartDate().toString(),
                savedProject.getEndDate().toString(),
                savedUser.getFirstName() + " " + savedUser.getLastName()
        );

        // ==========================
        // Create Welcome Task
        // ==========================

        // ==========================
// Create Welcome Task
// ==========================

        Task welcomeTask = createTask(
                "Welcome to Project Management System",
                "Complete the onboarding process.",
                Priority.MEDIUM,
                savedProject,
                savedUser
        );

        workspaceKnowledgeIndexer.indexTask(
                savedOrganization.getId(),
                savedProject.getId(),
                welcomeTask.getId(),
                savedProject.getName(),
                welcomeTask.getTitle(),
                welcomeTask.getDescription(),
                welcomeTask.getStatus().name(),
                welcomeTask.getPriority().name(),
                savedUser.getFirstName() + " " + savedUser.getLastName(),
                savedUser.getFirstName() + " " + savedUser.getLastName()
        );


// ==========================
// Create Invite Team Task
// ==========================

        Task inviteTask = createTask(
                "Invite your team members",
                "Invite users to your organization.",
                Priority.HIGH,
                savedProject,
                savedUser
        );

        workspaceKnowledgeIndexer.indexTask(
                savedOrganization.getId(),
                savedProject.getId(),
                inviteTask.getId(),
                savedProject.getName(),
                inviteTask.getTitle(),
                inviteTask.getDescription(),
                inviteTask.getStatus().name(),
                inviteTask.getPriority().name(),
                savedUser.getFirstName() + " " + savedUser.getLastName(),
                savedUser.getFirstName() + " " + savedUser.getLastName()
        );




        // ==========================
        // Response
        // ==========================

        OrganizationResponse response =
                modelMapper.map(savedOrganization, OrganizationResponse.class);

        response.setManagerId(savedUser.getId());
        response.setManagerName(
                savedUser.getFirstName() + " " + savedUser.getLastName());
        response.setManagerEmail(savedUser.getEmail());

        return response;
    }

    private Task createTask(
            String name,
            String description,
            Priority priority,
            Project project,
            User owner) {

        Task task = new Task();

        task.setTitle(name);
        task.setDescription(description);
        task.setPriority(priority);
        task.setStatus(TaskStatus.TODO);

        task.setProject(project);
        task.setCreatedBy(owner);
        task.setAssignedTo(owner);

        return taskRepository.save(task);
    }

    @Override
    public OrganizationResponse getOrganization(UUID id) {

        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        return modelMapper.map(organization, OrganizationResponse.class);
    }

    @Override
    public List<OrganizationResponse> getAllOrganizations() {

        return organizationRepository.findAll()
                .stream()
                .map(org -> modelMapper.map(org, OrganizationResponse.class))
                .toList();
    }

    @Override
    public OrganizationResponse updateOrganization(UUID id, OrganizationRequest request) {

        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        organization.setName(request.getName());
        organization.setIndustry(request.getIndustry());
        organization.setEmail(request.getEmail());
        organization.setPhone(request.getPhone());
        organization.setAddress(request.getAddress());

        Organization updatedOrganization = organizationRepository.save(organization);

        return modelMapper.map(updatedOrganization, OrganizationResponse.class);
    }

    @Override
    public void deleteOrganization(UUID id) {

        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        organizationRepository.delete(organization);
    }
}