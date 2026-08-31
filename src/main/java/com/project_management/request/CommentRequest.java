package com.project_management.request;

import lombok.Data;

import java.util.UUID;

@Data
public class CommentRequest {

    private String message;
    private UUID taskId;
}