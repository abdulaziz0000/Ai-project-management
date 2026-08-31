package com.project_management.controller;

import com.project_management.request.AcceptInvitationRequest;
import com.project_management.request.InviteUserRequest;
import com.project_management.response.AcceptInvitationResponse;
import com.project_management.response.InvitationValidationResponse;
import com.project_management.service.InvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<String> inviteUser(@RequestBody InviteUserRequest request) {

        System.out.println("Inside Invitation Controller");

        invitationService.inviteUser(request);

        return ResponseEntity.ok("Invitation sent successfully");
    }


    @GetMapping("/validate")
    public InvitationValidationResponse validateInvitation(
            @RequestParam String token) {
        System.out.println("Validate endpoint hit");


        return invitationService.validateInvitation(token);
    }

    @PostMapping("/accept")
    public ResponseEntity<AcceptInvitationResponse> acceptInvitation(
            @RequestBody AcceptInvitationRequest request) {

        return ResponseEntity.ok(invitationService.acceptInvitation(request));
    }
    @PostMapping("/{id}/resend")
    public void resendInvitation(@PathVariable UUID id) {
        invitationService.resendInvitation(id);
    }

    @DeleteMapping("/{id}")
    public void revokeInvitation(@PathVariable UUID id) {
        invitationService.revokeInvitation(id);
    }
}