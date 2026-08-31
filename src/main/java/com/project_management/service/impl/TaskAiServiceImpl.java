package com.project_management.service.impl;

import com.project_management.request.TaskSummary;
import com.project_management.response.ActionItemResponse;
import com.project_management.response.CommentResponse;
import com.project_management.response.RiskAnalysis;
import com.project_management.response.StandupResponse;
import com.project_management.service.TaskAiService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskAiServiceImpl implements TaskAiService {

    private final ChatClient chatClient;

    public TaskAiServiceImpl(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @Override
    public TaskSummary summarize(List<CommentResponse> comments) {

        String prompt = """
        Analyze the following task comments.

        Return a structured summary containing:

        currentStatus:
        Describe the current state of the task.

        keyDecisions:
        Include ONLY decisions that were actually agreed upon.
        Do NOT include:
        - action items
        - promises to do something
        - blockers
        - requests
        - future work

        blockers:
        Include issues, dependencies, missing approvals, failures,
        or anything preventing the task from progressing.

        Only use information explicitly present in the comments.
        Do not invent information.

        Task Comments:

        """ + comments.stream()
                .map(CommentResponse::getMessage)
                .collect(Collectors.joining("\n"));
        return chatClient.prompt()
                .user(prompt)
                .call()
                .entity(TaskSummary.class);
    }


    @Override
    public RiskAnalysis analyzeRisk(List<CommentResponse> comments) {

        String prompt = """
            Analyze the following task comments for project risk.

            Determine the risk level using:

            LOW
            MEDIUM
            HIGH

            Return:

            riskLevel:
            The overall risk level.

            reasons:
            Specific reasons why the task has this risk level.

            suggestions:
            Practical actions that could reduce the risk.

            Rules:

            - Only use information from the comments.
            - Do not invent problems.
            - Consider blockers, dependencies, failures,
              delays, missing approvals, and unclear requirements.
            - Keep the response concise.

            Task Comments:

            """ + comments.stream()
                .map(CommentResponse::getMessage)
                .collect(Collectors.joining("\n"));

        return chatClient.prompt()
                .user(prompt)
                .call()
                .entity(RiskAnalysis.class);
    }

    @Override
    public StandupResponse generateStandup(List<CommentResponse> comments) {

        String commentsText = comments.stream()
                .map(comment -> String.format(
                        "Developer: %s | Time: %s | Comment: %s",
                        comment.getUserName(),
                        comment.getCreatedAt(),
                        comment.getMessage()
                ))
                .collect(Collectors.joining("\n"));

        String prompt = """
        Generate a daily standup report from the following
        developer comments from the last 24 hours.

        Group the report by developer.

        For each developer identify:

        completed:
        Work explicitly described as completed, finished, done,
        merged, delivered, or resolved.

        inProgress:
        Work that is currently being worked on, being implemented,
        being fixed, or described as almost complete.

        blocked:
        Work that cannot proceed because it is waiting for an API,
        QA, approval, another developer, dependency, or external factor.

        Rules:

        - Group comments by developer.
        - ALWAYS include the task title in every item.
        - Format every item as:
          "Task Title — concise description"
        - Use only information present in the comments.
        - Do not invent work or task status.
        - "I will fix..." or "I am working on..." should normally
          be classified as inProgress.
        - Waiting for approval, QA, an API, another person,
          or another dependency should be classified as blocked.
        - Completed means the comment explicitly indicates completion.
        - Keep each item concise.
        - If there are no completed items, return [].
        - If there are no in-progress items, return [].
        - If there are no blockers, return [].

        Developer comments:

        """ + commentsText;

        return chatClient.prompt()
                .user(prompt)
                .call()
                .entity(StandupResponse.class);
    }

    @Override
    public ActionItemResponse extractActionItems(
            List<CommentResponse> comments) {

        String commentsText = comments.stream()
                .map(comment -> String.format(
                        "Developer: %s | Task: %s | Comment: %s",
                        comment.getUserName(),
                        comment.getTaskTitle(),
                        comment.getMessage()
                ))
                .collect(Collectors.joining("\n"));

        String prompt = """
        Analyze the following task comments and extract actionable work items.

        Return only genuine action items.

        For each action item return:

        assignee:
        The person responsible for completing the action.

        task:
        A concise description of the work to be done.

        priority:
        LOW, MEDIUM, or HIGH.

        deadline:
        The deadline explicitly mentioned in the comment.
        If no deadline is mentioned, return "Not specified".

        Rules:

        - Only extract actual actionable work.
        - A problem, failure, blocker, or current status is NOT
          an action item by itself.
        - For example:
          "Token validation is failing."
          is NOT an action item.
        - "I will fix the token validation issue today."
          IS an action item.
        - Do not invent an assignee.
        - Do not invent a deadline.
        - If the comment says "@john", assign it to John.
        - "please review", "update", "fix", "deploy",
          "implement", "test", etc. are examples of actions.
        - If priority is not explicitly stated, infer it carefully
          from the context.
        - If there are multiple comments describing the same action,
          return only ONE action item.
        - Combine duplicate or semantically identical actions.
        - Prefer the most specific version of the action.
        - Preserve the most specific deadline available.
        - If there is no clear action item, do not include it.

        Comments:

        """ + commentsText;

        return chatClient.prompt()
                .user(prompt)
                .call()
                .entity(ActionItemResponse.class);
    }

}