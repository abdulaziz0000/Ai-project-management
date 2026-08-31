package com.project_management.controller;

import com.project_management.request.WorkspaceQuestionRequest;
import com.project_management.response.WorkspaceAnswerResponse;
import com.project_management.service.WorkspaceAiService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/ai/workspace")
public class WorkspaceAiController {

    private final WorkspaceAiService workspaceAiService;

    public WorkspaceAiController(
            WorkspaceAiService workspaceAiService) {

        this.workspaceAiService = workspaceAiService;
    }

    @PostMapping("/ask")
    public WorkspaceAnswerResponse askWorkspace(
            @RequestParam UUID organizationId,
            @RequestBody WorkspaceQuestionRequest request) {

        String answer =
                workspaceAiService.askWorkspace(
                        organizationId,
                        request.getConversationId(),
                        request.getQuestion()
                );

        return new WorkspaceAnswerResponse(
                request.getConversationId(),
                answer
        );
    }
}