package com.project_management.response;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class CommentResponse {

    private UUID id;
    private String message;
    private UUID taskId;
    private String taskTitle;
    private UUID userId;
    private String userName;
    private Instant createdAt;
}
