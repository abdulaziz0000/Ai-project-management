package com.project_management.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class WorkspaceAnswerResponse {

    private UUID conversationId;

    private String answer;
}