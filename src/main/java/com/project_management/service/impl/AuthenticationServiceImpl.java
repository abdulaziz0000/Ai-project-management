package com.project_management.service.impl;

import com.project_management.entity.PasswordResetToken;
import com.project_management.entity.User;
import com.project_management.repository.PasswordResetTokenRepository;
import com.project_management.repository.UserRepository;
import com.project_management.request.ForgotPasswordRequest;
import com.project_management.request.LoginRequest;
import com.project_management.response.AuthenticationResponse;
import com.project_management.response.OrganizationLoginResponse;
import com.project_management.response.UserLoginResponse;
import com.project_management.security.JwtService;
import com.project_management.service.AuthenticationService;
import com.project_management.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend-url}")
    private String appFrontendUrl;
    @Override
    public AuthenticationResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        String jwt = jwtService.generateToken(user);

        UserLoginResponse userResponse = new UserLoginResponse();
        userResponse.setId(user.getId());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setEmail(user.getEmail());
        userResponse.setRole(user.getRole().name());

        OrganizationLoginResponse organizationResponse =
                new OrganizationLoginResponse();

        organizationResponse.setId(user.getOrganization().getId());
        organizationResponse.setName(user.getOrganization().getName());

        AuthenticationResponse response =
                new AuthenticationResponse();

        response.setAccessToken(jwt);
        response.setExpiresIn(3600000L);
        response.setUser(userResponse);
        response.setOrganization(organizationResponse);

        return response;
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        passwordResetTokenRepository
                .deleteByUserId(user.getId());

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken =
                PasswordResetToken.builder()
                        .token(token)
                        .userId(user.getId())
                        .expiresAt(
                                LocalDateTime.now()
                                        .plusMinutes(15)
                        )
                        .used(false)
                        .build();

        passwordResetTokenRepository.save(resetToken);

        String resetLink =
                appFrontendUrl + "/reset-password?token=" + token;
        String subject =
                "Password Reset - Project Management System";

        String body =
                """
                Hello %s,

                We received a request to reset your password.

                Please click the link below to reset your password:

                %s

                This link will expire in 15 minutes.

                If you did not request a password reset,
                please ignore this email.

                Regards,
                Project Management System
                """.formatted(
                        user.getFirstName(),
                        resetLink
                );

        emailService.sendEmail(
                user.getEmail(),
                subject,
                body
        );
    }

    @Override
    public void resetPassword(
            String token,
            String newPassword) {

        // 1. Find reset token
        PasswordResetToken resetToken =
                passwordResetTokenRepository
                        .findByToken(token)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid password reset token"
                                ));

        // 2. Check whether token was already used
        if (resetToken.isUsed()) {
            throw new RuntimeException(
                    "Password reset token has already been used"
            );
        }

        // 3. Check token expiration
        if (resetToken.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "Password reset token has expired"
            );
        }

        // 4. Find the user
        User user = userRepository
                .findById(resetToken.getUserId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        ));

        // 5. Encrypt the new password
        String encodedPassword =
                passwordEncoder.encode(newPassword);

        user.setPassword(encodedPassword);

        // 6. Save updated user
        userRepository.save(user);

        // 7. Mark token as used
        resetToken.setUsed(true);

        passwordResetTokenRepository.save(resetToken);
    }
}
