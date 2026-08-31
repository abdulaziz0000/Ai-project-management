package com.project_management.response;

import lombok.Data;

@Data
public class AuthenticationResponse {

    private String accessToken;

    private Long expiresIn;

    private UserLoginResponse user;

    private OrganizationLoginResponse organization;

}