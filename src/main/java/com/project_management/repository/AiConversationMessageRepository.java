package com.project_management.repository;

import com.project_management.entity.AiConversationMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AiConversationMessageRepository
        extends JpaRepository<AiConversationMessage, UUID> {

    List<AiConversationMessage>
    findByConversationIdAndOrganizationIdOrderByCreatedAtAsc(
            UUID conversationId,
            UUID organizationId
    );
}