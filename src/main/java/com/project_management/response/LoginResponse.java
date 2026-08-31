package com.project_management.response;

import com.project_management.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

    private String accessToken;

    private Long expiresIn;

    private UUID userId;

    private String organizationName;
    private UUID organizationId;

    private String firstName;

    private String lastName;

    private String email;

    private Role role;
}