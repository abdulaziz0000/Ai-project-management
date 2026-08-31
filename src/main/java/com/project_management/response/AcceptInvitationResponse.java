package com.project_management.response;

import lombok.Data;

import java.util.UUID;

@Data
public class AcceptInvitationResponse {

    private UserResponse user;

    private UUID organizationId;

    private UUID projectId;

    private UUID managerId;
}