package com.project_management.service.impl;

import com.project_management.entity.Comment;
import com.project_management.entity.Task;
import com.project_management.entity.User;
import com.project_management.ingestion.WorkspaceKnowledgeIndexer;
import com.project_management.repository.CommentRepository;
import com.project_management.repository.TaskRepository;
import com.project_management.repository.UserRepository;
import com.project_management.request.CommentRequest;
import com.project_management.response.CommentResponse;
import com.project_management.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final WorkspaceKnowledgeIndexer workspaceKnowledgeIndexer;

    @Override
    public CommentResponse addComment(
            CommentRequest request,
            String email) {

        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new RuntimeException("Task not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = new Comment();

        comment.setMessage(request.getMessage());
        comment.setTask(task);
        comment.setUser(user);

        Comment saved = commentRepository.save(comment);
        System.out.println("========== RAG DEBUG ==========");
        System.out.println("Task ID: " + task.getId());
        System.out.println("Project ID: " + task.getProject().getId());
        System.out.println("Project Name: " + task.getProject().getName());
        System.out.println("Organization ID: " +
                task.getProject().getOrganization().getId());
        System.out.println("Organization Name: " +
                task.getProject().getOrganization().getName());
        System.out.println("==============================");

        // ==============================
        // RAG INDEXING
        // ==============================
        workspaceKnowledgeIndexer.indexComment(
                task.getProject().getOrganization().getId(),
                task.getProject().getId(),
                task.getId(),
                saved.getId(),
                task.getProject().getName(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus() != null
                        ? task.getStatus().toString()
                        : "Not specified",
                task.getPriority() != null
                        ? task.getPriority().toString()
                        : "Not specified",
                task.getAssignedTo() != null
                        ? task.getAssignedTo().getFirstName()
                        : "Not assigned",
                user.getFirstName(),
                saved.getMessage()
        );
        // ==============================
        // RESPONSE
        // ==============================

        CommentResponse response = new CommentResponse();

        response.setId(saved.getId());
        response.setMessage(saved.getMessage());
        response.setTaskId(saved.getTask().getId());
        response.setUserId(saved.getUser().getId());
        response.setUserName(saved.getUser().getFirstName());
        response.setCreatedAt(saved.getCreatedAt());

        return response;
    }
    @Override
    public CommentResponse updateComment(UUID id, CommentRequest request) {

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        comment.setMessage(request.getMessage());

        Comment updatedComment = commentRepository.save(comment);

        return modelMapper.map(updatedComment, CommentResponse.class);
    }

    @Override
    public void deleteComment(UUID id) {

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        commentRepository.delete(comment);
    }

    @Override
    public List<CommentResponse> getTaskComments(UUID taskId) {

        return commentRepository.findByTaskId(taskId)
                .stream()
                .map(comment -> {

                    CommentResponse response = new CommentResponse();

                    response.setId(comment.getId());
                    response.setMessage(comment.getMessage());
                    response.setTaskId(comment.getTask().getId());
                    response.setTaskTitle(comment.getTask().getTitle());
                    response.setUserId(comment.getUser().getId());
                    response.setUserName(comment.getUser().getFirstName());
                    response.setCreatedAt(comment.getCreatedAt());

                    return response;

                })
                .toList();
    }

    @Override
    public List<CommentResponse> getProjectCommentsForStandup(UUID projectId) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.minusDays(1);

        List<Comment> comments =
                commentRepository.findProjectCommentsBetween(
                        projectId,
                        from,
                        now
                );

        return comments.stream()
                .map(comment -> {

                    CommentResponse response = new CommentResponse();

                    response.setId(comment.getId());
                    response.setMessage(comment.getMessage());
                    response.setCreatedAt(comment.getCreatedAt());
                    response.setTaskTitle(comment.getTask().getTitle());

                    response.setTaskId(comment.getTask().getId());

                    response.setUserId(comment.getUser().getId());
                    response.setUserName(comment.getUser().getFirstName());

                    return response;
                })
                .toList();
    }
}