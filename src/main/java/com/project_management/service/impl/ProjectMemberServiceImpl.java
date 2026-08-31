package com.project_management.service.impl;

import com.project_management.entity.ProjectMember;
import com.project_management.repository.ProjectMemberRepository;
import com.project_management.response.UserResponse;
import com.project_management.service.ProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectMemberServiceImpl implements ProjectMemberService {
    private final ProjectMemberRepository projectMemberRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<UserResponse> getProjectMembers(UUID projectId) {

        List<ProjectMember> members =
                projectMemberRepository.findByProjectId(projectId);

        return members.stream()
                .map(ProjectMember::getUser)
                .map(user -> modelMapper.map(user, UserResponse.class))
                .toList();
    }
}
