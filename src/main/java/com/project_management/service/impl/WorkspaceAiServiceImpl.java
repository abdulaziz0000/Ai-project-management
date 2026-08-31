package com.project_management.service.impl;

import com.project_management.entity.AiConversationMessage;
import com.project_management.enums.MessageRole;
import com.project_management.repository.AiConversationMessageRepository;
import com.project_management.service.WorkspaceAiService;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WorkspaceAiServiceImpl
        implements WorkspaceAiService {

    private final VectorStore vectorStore;

    private final ChatClient chatClient;

    private final AiConversationMessageRepository
            conversationRepository;


    public WorkspaceAiServiceImpl(
            VectorStore vectorStore,
            ChatClient.Builder chatClientBuilder,
            AiConversationMessageRepository conversationRepository) {

        this.vectorStore = vectorStore;

        this.chatClient =
                chatClientBuilder.build();

        this.conversationRepository =
                conversationRepository;
    }


    @Override
    public String askWorkspace(
            UUID organizationId,
            UUID conversationId,
            String question) {


        /*
         * ============================================================
         * 1. LOAD CONVERSATION HISTORY
         * ============================================================
         */

        List<AiConversationMessage> history =
                conversationRepository
                        .findByConversationIdAndOrganizationIdOrderByCreatedAtAsc(
                                conversationId,
                                organizationId
                        );


        /*
         * Convert history into readable text.
         */

        String conversationHistory =
                history.stream()

                        .map(message ->
                                message.getRole()
                                        + ": "
                                        + message.getContent()
                        )

                        .collect(
                                Collectors.joining("\n")
                        );


        /*
         * ============================================================
         * 2. REWRITE USER QUESTION
         * ============================================================
         *
         * Example:
         *
         * Previous:
         *
         * USER:
         * What task is Maliha working on?
         *
         * ASSISTANT:
         * Maliha is working on Build the frontend UI first.
         *
         * Current:
         *
         * What's its priority?
         *
         * Rewritten:
         *
         * What is the priority of the task
         * Build the frontend UI first assigned to Maliha?
         *
         * ============================================================
         */

        String rewrittenQuestion =
                rewriteQuestion(
                        question,
                        conversationHistory
                );


        /*
         * ============================================================
         * 3. RAG VECTOR SEARCH
         * ============================================================
         *
         * IMPORTANT:
         *
         * We search using rewrittenQuestion,
         * NOT the original question.
         *
         * ============================================================
         */

        List<Document> documents =
                vectorStore.similaritySearch(

                        SearchRequest.builder()

                                .query(rewrittenQuestion)

                                .topK(5)

                                .similarityThreshold(0.70)

                                /*
                                 * Security:
                                 *
                                 * Only retrieve documents
                                 * belonging to this organization.
                                 */

                                .filterExpression(
                                        "organizationId == '"
                                                + organizationId
                                                + "'"
                                )

                                .build()
                );


        /*
         * ============================================================
         * 4. BUILD WORKSPACE CONTEXT
         * ============================================================
         */

        String context =
                documents.stream()

                        .map(document -> {

                            String metadata =
                                    document.getMetadata()
                                            .toString();

                            return """
                                    DOCUMENT METADATA:
                                    %s

                                    DOCUMENT CONTENT:
                                    %s
                                    """
                                    .formatted(
                                            metadata,
                                            document.getText()
                                    );
                        })

                        .collect(
                                Collectors.joining(
                                        "\n\n--------------------\n\n"
                                )
                        );


        /*
         * ============================================================
         * 5. BUILD FINAL ANSWER PROMPT
         * ============================================================
         */

        String prompt = """

                You are the Workspace Knowledge Assistant
                for a project management system.

                Your job is to answer questions about the
                user's organization, projects, tasks,
                team members, and comments.

                You have two sources of information:

                1. Conversation History
                2. Workspace Context retrieved using RAG

                ====================================================
                IMPORTANT RULES
                ====================================================

                - Use ONLY information from the conversation
                  history and workspace context.

                - Never invent information.

                - Never assume information that is not present.

                - If the answer cannot be determined from
                  the available information, respond:

                  "The information is not available."

                - Conversation history can be used to understand
                  references such as:

                  "it"
                  "this"
                  "that"
                  "that task"
                  "that project"
                  "their"
                  "he"
                  "she"
                  "they"

                - The rewritten question represents the user's
                  intended meaning.

                - Answer the user's original question naturally.

                - When relevant, identify:

                  Project
                  Task
                  Task description
                  Task status
                  Task priority
                  Assigned user
                  Comment author
                  Blocker
                  Relevant comment

                - Do not mention irrelevant fields.

                - Keep the answer concise but specific.

                ====================================================
                CONVERSATION HISTORY
                ====================================================

                %s


                ====================================================
                REWRITTEN QUESTION
                ====================================================

                %s


                ====================================================
                WORKSPACE CONTEXT
                ====================================================

                %s


                ====================================================
                ORIGINAL USER QUESTION
                ====================================================

                %s

                """
                .formatted(
                        conversationHistory,
                        rewrittenQuestion,
                        context,
                        question
                );


        /*
         * ============================================================
         * 6. CALL LLM
         * ============================================================
         */

        String answer =
                chatClient.prompt()

                        .user(prompt)

                        .call()

                        .content();


        /*
         * ============================================================
         * 7. SAVE USER MESSAGE
         * ============================================================
         */

        AiConversationMessage userMessage =
                new AiConversationMessage();

        userMessage.setConversationId(
                conversationId
        );

        userMessage.setOrganizationId(
                organizationId
        );

        userMessage.setRole(
                MessageRole.USER
        );

        userMessage.setContent(
                question
        );

        userMessage.setCreatedAt(
                LocalDateTime.now()
        );

        conversationRepository.save(
                userMessage
        );


        /*
         * ============================================================
         * 8. SAVE ASSISTANT RESPONSE
         * ============================================================
         */

        AiConversationMessage assistantMessage =
                new AiConversationMessage();

        assistantMessage.setConversationId(
                conversationId
        );

        assistantMessage.setOrganizationId(
                organizationId
        );

        assistantMessage.setRole(
                MessageRole.ASSISTANT
        );

        assistantMessage.setContent(
                answer
        );

        assistantMessage.setCreatedAt(
                LocalDateTime.now()
        );

        conversationRepository.save(
                assistantMessage
        );


        /*
         * ============================================================
         * 9. RETURN ANSWER
         * ============================================================
         */

        return answer;
    }


    /*
     * ================================================================
     * QUERY REWRITER
     * ================================================================
     */

    private String rewriteQuestion(
            String question,
            String conversationHistory) {


        /*
         * If there is no conversation history,
         * there is nothing to resolve.
         */

        if (conversationHistory == null
                || conversationHistory.isBlank()) {

            return question;
        }


        String prompt = """

                You are a query rewriting component
                for a project management knowledge system.

                Your job is to rewrite the user's latest
                question into a standalone question.

                Use the conversation history to understand
                what the user is referring to.

                Resolve references such as:

                - it
                - this
                - that
                - this task
                - that task
                - this project
                - that project
                - he
                - she
                - they
                - their
                - its

                Example:

                Conversation:

                USER:
                What task is Maliha working on?

                ASSISTANT:
                Maliha is working on Build the frontend UI first.

                Latest question:

                What is its priority?

                Rewrite as:

                What is the priority of the task
                Build the frontend UI first assigned to Maliha?

                ====================================================

                Rules:

                - Do NOT answer the question.
                - Return ONLY the rewritten question.
                - Do not add explanations.
                - Do not invent information.
                - Preserve the user's original intent.
                - If the question is already standalone,
                  return it unchanged.

                ====================================================
                CONVERSATION HISTORY
                ====================================================

                %s


                ====================================================
                LATEST USER QUESTION
                ====================================================

                %s

                """
                .formatted(
                        conversationHistory,
                        question
                );


        return chatClient.prompt()

                .user(prompt)

                .call()

                .content()

                .trim();
    }
}