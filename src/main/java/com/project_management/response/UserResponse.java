package com.project_management.response;

import com.project_management.enums.Role;
import com.project_management.enums.UserStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserResponse {

    private UUID id;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private String designation;

    private Role role;

    private UserStatus status;

    private boolean enabled;

    private LocalDateTime createdAt;

    private UUID organizationId;

    private String organizationName;
}