package com.project_management.request;

import com.project_management.enums.Role;
import lombok.Data;

import java.util.UUID;

@Data
public class InviteUserRequest {

    private String email;
    private String designation;
    private Role role;
    private UUID organizationId;
    private UUID projectId;
    private UUID invitedBy;
}