package com.project_management.response;

import lombok.Data;

import java.util.UUID;

@Data
public class UserLoginResponse {

    private UUID id;

    private String firstName;

    private String lastName;

    private String email;

    private String role;

}