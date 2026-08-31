package com.project_management.controller;

import com.project_management.request.TaskSummary;
import com.project_management.response.ActionItemResponse;
import com.project_management.response.CommentResponse;
import com.project_management.response.RiskAnalysis;
import com.project_management.response.StandupResponse;
import com.project_management.service.CommentService;
import com.project_management.service.TaskAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks/ai")
@RequiredArgsConstructor
public class TaskAiController {

    private final TaskAiService taskAiService;
    private final CommentService commentService;

    @GetMapping("/{taskId}/summary")
    public TaskSummary summary(@PathVariable UUID taskId) {

        return taskAiService.summarize(
                commentService.getTaskComments(taskId)
        );
    }

    @GetMapping("/{taskId}/risk")
    public RiskAnalysis risk(@PathVariable UUID taskId) {

        return taskAiService.analyzeRisk(
                commentService.getTaskComments(taskId)
        );
    }

    @GetMapping("/project/{projectId}/standup")
    public StandupResponse standup(
            @PathVariable UUID projectId) {

        List<CommentResponse> comments =
                commentService.getProjectCommentsForStandup(projectId);

        return taskAiService.generateStandup(comments);
    }

    @GetMapping("/{taskId}/action-items")
    public ActionItemResponse actionItems(
            @PathVariable UUID taskId) {

        List<CommentResponse> comments =
                commentService.getTaskComments(taskId);

        return taskAiService.extractActionItems(comments);
    }
}