package com.project_management.service;

import com.project_management.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface ProjectMemberService {

    List<UserResponse> getProjectMembers(UUID projectId);

}