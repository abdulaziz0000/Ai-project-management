package com.project_management.request;

import lombok.Data;

import java.util.List;

@Data
public class TaskSummary {

    private String currentStatus;

    private List<String> keyDecisions;

    private List<String> blockers;

}
