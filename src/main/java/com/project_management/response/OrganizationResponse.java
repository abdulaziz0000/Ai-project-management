package com.project_management.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class OrganizationResponse {


    private UUID id;

    private String name;

    private String industry;

    private String email;

    private String phone;

    private String address;

    private LocalDateTime createdAt;

    private UUID managerId;
    private String managerName;
    private String managerEmail;
}
