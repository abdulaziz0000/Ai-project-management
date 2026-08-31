package com.project_management.response;

import lombok.Data;

import java.util.List;

@Data
public class DeveloperStandup {

    private String developer;

    private List<String> completed;

    private List<String> inProgress;

    private List<String> blocked;
}