package com.project_management.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvitationValidationResponse {

    private boolean valid;

    private String email;

    private String organizationName;

    private String projectName;

    private String role;

    private String invitedBy;

    private String designation;

}