package com.project_management.response;

import lombok.Data;

import java.util.List;

@Data
public class StandupResponse {

    private List<DeveloperStandup> developers;
}