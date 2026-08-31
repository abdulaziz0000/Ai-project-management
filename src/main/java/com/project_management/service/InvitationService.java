package com.project_management.service;

import com.project_management.request.AcceptInvitationRequest;
import com.project_management.request.InviteUserRequest;
import com.project_management.response.AcceptInvitationResponse;
import com.project_management.response.InvitationValidationResponse;
import com.project_management.response.UserResponse;

import java.util.UUID;

public interface InvitationService {

    void inviteUser(InviteUserRequest request);

    AcceptInvitationResponse acceptInvitation(AcceptInvitationRequest request);
    InvitationValidationResponse validateInvitation(String token);

    void resendInvitation(UUID invitationId);

    void revokeInvitation(UUID invitationId);

}