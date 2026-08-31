package com.project_management.service;

import com.project_management.request.ForgotPasswordRequest;
import com.project_management.request.LoginRequest;
import com.project_management.response.AuthenticationResponse;

public interface AuthenticationService {

    AuthenticationResponse login(LoginRequest request);

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(String token, String newPassword);
}