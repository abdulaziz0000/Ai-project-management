package com.project_management.ingestion;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class WorkspaceKnowledgeIndexer {

    private final VectorStore vectorStore;

    public WorkspaceKnowledgeIndexer(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    // ============================================================
    // PROJECT
    // ============================================================

    public void indexProject(
            UUID organizationId,
            UUID projectId,
            String projectName,
            String projectDescription,
            String projectStatus,
            String projectPriority,
            String startDate,
            String endDate,
            String createdBy) {

        String content = """
                Project: %s

                Project Description:
                %s

                Project Status:
                %s

                Project Priority:
                %s

                Start Date:
                %s

                End Date:
                %s

                Created By:
                %s
                """.formatted(
                projectName,
                projectDescription,
                projectStatus,
                projectPriority,
                startDate,
                endDate,
                createdBy
        );

        Map<String, Object> metadata = new HashMap<>();

        metadata.put("organizationId", organizationId.toString());
        metadata.put("projectId", projectId.toString());
        metadata.put("documentType", "PROJECT");

        Document document = new Document(
                content,
                metadata
        );

        vectorStore.add(List.of(document));

        System.out.println("========== PROJECT INDEXED ==========");
        System.out.println("Organization: " + organizationId);
        System.out.println("Project: " + projectName);
        System.out.println("Project ID: " + projectId);
        System.out.println("=====================================");
    }


    // ============================================================
    // TASK
    // ============================================================

    public void indexTask(
            UUID organizationId,
            UUID projectId,
            UUID taskId,
            String projectName,
            String taskTitle,
            String taskDescription,
            String taskStatus,
            String taskPriority,
            String assignedTo,
            String createdBy) {

        String content = """
                Project: %s

                Task: %s

                Task Description:
                %s

                Task Status:
                %s

                Task Priority:
                %s

                Assigned To:
                %s

                Created By:
                %s
                """.formatted(
                projectName,
                taskTitle,
                taskDescription,
                taskStatus,
                taskPriority,
                assignedTo,
                createdBy
        );

        Map<String, Object> metadata = new HashMap<>();

        metadata.put("organizationId", organizationId.toString());
        metadata.put("projectId", projectId.toString());
        metadata.put("taskId", taskId.toString());
        metadata.put("documentType", "TASK");

        Document document = new Document(
                content,
                metadata
        );

        vectorStore.add(List.of(document));

        System.out.println("========== TASK INDEXED ==========");
        System.out.println("Organization: " + organizationId);
        System.out.println("Project: " + projectName);
        System.out.println("Task: " + taskTitle);
        System.out.println("Task ID: " + taskId);
        System.out.println("==================================");
    }


    // ============================================================
    // COMMENT
    // ============================================================

    public void indexComment(
            UUID organizationId,
            UUID projectId,
            UUID taskId,
            UUID commentId,
            String projectName,
            String taskTitle,
            String taskDescription,
            String taskStatus,
            String taskPriority,
            String assignedTo,
            String commentBy,
            String comment) {

        String content = """
                Project: %s

                Task: %s

                Task Description:
                %s

                Task Status:
                %s

                Task Priority:
                %s

                Assigned To:
                %s

                Comment By:
                %s

                Comment:
                %s
                """.formatted(
                projectName,
                taskTitle,
                taskDescription,
                taskStatus,
                taskPriority,
                assignedTo,
                commentBy,
                comment
        );

        Map<String, Object> metadata = new HashMap<>();

        metadata.put("organizationId", organizationId.toString());
        metadata.put("projectId", projectId.toString());
        metadata.put("taskId", taskId.toString());
        metadata.put("commentId", commentId.toString());
        metadata.put("documentType", "COMMENT");

        Document document = new Document(
                content,
                metadata
        );

        vectorStore.add(List.of(document));

        System.out.println("========== COMMENT INDEXED ==========");
        System.out.println("Organization: " + organizationId);
        System.out.println("Project: " + projectName);
        System.out.println("Task: " + taskTitle);
        System.out.println("Comment ID: " + commentId);
        System.out.println("=====================================");
    }
}