package com.project_management.service;

import java.util.UUID;

public interface WorkspaceAiService {

    String askWorkspace(
            UUID organizationId,
            UUID conversationId,
            String question
    );
}