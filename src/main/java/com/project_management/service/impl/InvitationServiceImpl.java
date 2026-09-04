package com.project_management.service.impl;

import com.project_management.entity.*;
import com.project_management.enums.InvitationStatus;
import com.project_management.enums.UserStatus;
import com.project_management.repository.*;
import com.project_management.request.AcceptInvitationRequest;
import com.project_management.request.InviteUserRequest;
import com.project_management.response.AcceptInvitationResponse;
import com.project_management.response.InvitationValidationResponse;
import com.project_management.response.UserResponse;
import com.project_management.service.EmailService;
import com.project_management.service.InvitationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvitationServiceImpl implements InvitationService {

    private final InvitationRepository invitationRepository;
    private final OrganizationRepository organizationRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ProjectMemberRepository projectMemberRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend-url}")
    private String appFrontendUrl;

    @Transactional
    @Override
    public void inviteUser(InviteUserRequest request) {

        System.out.println(request);
        System.out.println("Organization: " + request.getOrganizationId());
        System.out.println("Project: " + request.getProjectId());
        System.out.println("InvitedBy: " + request.getInvitedBy());
        Organization organization =
                organizationRepository.findById(request.getOrganizationId())
                        .orElseThrow(() -> new RuntimeException("Organization not found"));

        Project project =
                projectRepository.findById(request.getProjectId())
                        .orElseThrow(() -> new RuntimeException("Project not found"));

        User invitedBy =
                userRepository.findById(request.getInvitedBy())
                        .orElseThrow(() -> new RuntimeException("User not found"));

        if (!project.getOrganization().getId().equals(organization.getId())) {
            throw new RuntimeException("Project does not belong to the organization");
        }
//
//        if (invitedBy.getRole() != Role.SUPER_ADMIN) {
//            throw new RuntimeException("Only Super Admin can invite users");
//        }

        if (!invitedBy.getOrganization().getId().equals(organization.getId())) {
            throw new RuntimeException("Invalid inviter");
        }

        // If a user with this email already has an account, they don't need to go
        // through registration again — just add them to this project directly,
        // as long as they aren't already a member of it.
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {

            User user = existingUser.get();

            boolean alreadyMember = projectMemberRepository
                    .findByProjectIdAndUserId(project.getId(), user.getId())
                    .isPresent();

            if (alreadyMember) {
                throw new RuntimeException("User is already a member of this project");
            }

            ProjectMember member = new ProjectMember();
            member.setUser(user);
            member.setProject(project);
            member.setRole(request.getRole());
            member.setActive(true);
            projectMemberRepository.save(member);

            String subject = "Added to Project";

            String body =
                    """
                    Hello,

                    %s added you to project %s.
                    """
                            .formatted(
                                    invitedBy.getFirstName(),
                                    project.getName()
                            );

            emailService.sendEmail(
                    request.getEmail(),
                    subject,
                    body
            );

            return;
        }

        if(invitationRepository.findByEmailAndStatus(
                request.getEmail(),
                InvitationStatus.PENDING).isPresent()){

            throw new RuntimeException("Invitation already sent");
        }

        String token = UUID.randomUUID().toString();

        Invitation invitation = new Invitation();

        invitation.setToken(token);

        invitation.setEmail(request.getEmail());

        invitation.setRole(request.getRole());

        invitation.setStatus(InvitationStatus.PENDING);

        invitation.setExpiresAt(
                LocalDateTime.now().plusDays(7)
        );

        invitation.setOrganization(organization);

        invitation.setProject(project);


        invitation.setInvitedBy(invitedBy);

        invitationRepository.save(invitation);

        String link =
                appFrontendUrl + "/register?token=" + token;

        String subject="Project Invitation";

        String body=
                """
                Hello,
                
                %s invited you to join project %s.
                
                Click below:
                
                %s
                
                This invitation expires in 7 days.
                """
                        .formatted(
                                invitedBy.getFirstName(),
                                project.getName(),
                                link
                        );

        emailService.sendEmail(
                request.getEmail(),
                subject,
                body
        );
    }

    @Transactional
    @Override
    public AcceptInvitationResponse acceptInvitation(AcceptInvitationRequest request) {

        Invitation invitation = invitationRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid invitation token"));

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new RuntimeException("Invitation is no longer valid");
        }

        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            invitation.setStatus(InvitationStatus.EXPIRED);
            invitationRepository.save(invitation);
            throw new RuntimeException("Invitation has expired");
        }

        // Create the user account
        User user = new User();
        user.setEmail(invitation.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );
        user.setRole(invitation.getRole());
        user.setOrganization(invitation.getOrganization());
        user.setPhone(request.getPhone());
        user.setDesignation(invitation.getDesignation());


        user.setStatus(UserStatus.ACTIVE);

        user.setEnabled(true);

        user.setRole(invitation.getRole());

        user.setOrganization(invitation.getOrganization());
        user = userRepository.save(user);

        // Add to project
        ProjectMember member = new ProjectMember();
        member.setUser(user);
        member.setProject(invitation.getProject());
        member.setRole(invitation.getRole());
        projectMemberRepository.save(member);

        // Mark invitation accepted
        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitationRepository.save(invitation);

        AcceptInvitationResponse response = new AcceptInvitationResponse();

        response.setUser(modelMapper.map(user, UserResponse.class));

        response.setOrganizationId(invitation.getOrganization().getId());

        response.setProjectId(invitation.getProject().getId());

        response.setManagerId(invitation.getInvitedBy().getId());

        return response;
    }

    @Override
    public InvitationValidationResponse validateInvitation(String token) {

        Invitation invitation =
                invitationRepository.findByToken(token)
                        .orElseThrow(() ->
                                new RuntimeException("Invalid invitation"));

        if(invitation.getStatus()!=InvitationStatus.PENDING){
            throw new RuntimeException("Invitation already used");
        }

        if(invitation.getExpiresAt().isBefore(LocalDateTime.now())){

            invitation.setStatus(InvitationStatus.EXPIRED);

            invitationRepository.save(invitation);

            throw new RuntimeException("Invitation expired");
        }

        InvitationValidationResponse response =
                new InvitationValidationResponse();

        response.setValid(true);

        response.setEmail(invitation.getEmail());

        response.setProjectName(
                invitation.getProject().getName());

        response.setOrganizationName(
                invitation.getOrganization().getName());

        response.setRole(
                invitation.getRole().name());

        response.setInvitedBy(
                invitation.getInvitedBy().getFirstName()
                        + " "
                        + invitation.getInvitedBy().getLastName());

        if (invitation.getDesignation() != null) {
            response.setDesignation(
                    invitation.getDesignation());
        }

        return response;
    }

    @Override
    public void resendInvitation(UUID invitationId) {

    }

    @Override
    public void revokeInvitation(UUID invitationId) {

    }
}
