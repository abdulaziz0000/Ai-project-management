package com.project_management.request;

import com.project_management.enums.Role;
import com.project_management.enums.UserStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class UserRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private String designation;
    private Role role;
    private UserStatus status;
    private UUID organizationId;
}