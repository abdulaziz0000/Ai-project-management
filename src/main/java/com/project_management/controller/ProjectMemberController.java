package com.project_management.controller;

import com.project_management.response.UserResponse;
import com.project_management.service.ProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/project-members")
@RequiredArgsConstructor
@CrossOrigin(origins = "https://ai-project-management.azizabdul04327.workers.dev")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    @GetMapping("/project/{projectId}")
    public List<UserResponse> getProjectMembers(
            @PathVariable UUID projectId) {

        return projectMemberService.getProjectMembers(projectId);
    }
}
