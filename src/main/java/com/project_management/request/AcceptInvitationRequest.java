package com.project_management.request;
import lombok.Data;

@Data
public class AcceptInvitationRequest {

    private String token;

    private String firstName;

    private String lastName;

    private String phone;

    private String password;
}