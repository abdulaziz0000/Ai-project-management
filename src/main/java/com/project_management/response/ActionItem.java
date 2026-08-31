package com.project_management.response;

import lombok.Data;

@Data
public class ActionItem {

    private String assignee;

    private String task;

    private String priority;

    private String deadline;
}