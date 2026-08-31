package com.project_management.service;

import com.project_management.entity.Comment;
import com.project_management.request.TaskSummary;
import com.project_management.response.ActionItemResponse;
import com.project_management.response.CommentResponse;
import com.project_management.response.RiskAnalysis;
import com.project_management.response.StandupResponse;

import java.util.List;

public interface TaskAiService {

    TaskSummary summarize(List<CommentResponse> comments);


    RiskAnalysis analyzeRisk(List<CommentResponse> comments);

    StandupResponse generateStandup(List<CommentResponse> comments);

    ActionItemResponse extractActionItems(List<CommentResponse> comments);
}
