package com.project_management.service;

import com.project_management.request.CommentRequest;
import com.project_management.response.CommentResponse;

import java.util.List;
import java.util.UUID;

public interface CommentService {

    CommentResponse addComment(CommentRequest request, String name);

    CommentResponse updateComment(UUID id,
                                  CommentRequest request);

    void deleteComment(UUID id);

    List<CommentResponse> getTaskComments(UUID taskId);



    List<CommentResponse> getProjectCommentsForStandup(UUID projectId);
}