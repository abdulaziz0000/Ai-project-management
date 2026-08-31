package com.project_management.service;

import com.project_management.request.LoginRequest;
import com.project_management.request.UserRequest;
import com.project_management.response.LoginResponse;
import com.project_management.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse getUser(UUID id);

    List<UserResponse> getUsersByOrganization(UUID organizationId);

    UserResponse updateUser(UUID id, UserRequest request);

    void deleteUser(UUID id);
//    LoginResponse login(LoginRequest request);

}