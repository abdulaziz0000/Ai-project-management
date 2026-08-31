package com.project_management.response;

import lombok.Data;

import java.util.List;

@Data
public class RiskAnalysis {

    private String riskLevel;

    private List<String> reasons;

    private List<String> suggestions;
}