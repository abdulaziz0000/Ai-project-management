package com.project_management.request;

import lombok.Data;

import java.util.UUID;

@Data
public class WorkspaceQuestionRequest {

    private UUID conversationId;

    private String question;
}