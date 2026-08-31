package com.project_management.controller;

import com.project_management.request.CommentRequest;
import com.project_management.response.CommentResponse;
import com.project_management.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public CommentResponse addComment(
            @RequestBody CommentRequest request,
            Authentication authentication) {

        return commentService.addComment(
                request,
                authentication.getName()
        );
    }
    @PutMapping("/{id}")
    public CommentResponse updateComment(@PathVariable UUID id,
                                         @RequestBody CommentRequest request) {
        return commentService.updateComment(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable UUID id) {
        commentService.deleteComment(id);
    }

    @GetMapping("/task/{taskId}")
    public List<CommentResponse> getTaskComments(@PathVariable UUID taskId) {
        return commentService.getTaskComments(taskId);
    }
}