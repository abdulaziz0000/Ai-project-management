package com.project_management.service.impl;

import com.project_management.entity.Organization;
import com.project_management.entity.ProjectMember;
import com.project_management.entity.User;
import com.project_management.repository.OrganizationRepository;
import com.project_management.repository.ProjectMemberRepository;
import com.project_management.repository.UserRepository;
import com.project_management.request.LoginRequest;
import com.project_management.request.UserRequest;
import com.project_management.response.LoginResponse;
import com.project_management.response.UserResponse;
import com.project_management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final ModelMapper modelMapper;
    private final ProjectMemberRepository projectMemberRepository;

    private PasswordEncoder passwordEncoder;

    @Override
    public UserResponse getUser(UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    public List<UserResponse> getUsersByOrganization(UUID organizationId) {

        return userRepository.findByOrganizationId(organizationId)
                .stream()
                .map(user -> modelMapper.map(user, UserResponse.class))
                .toList();
    }

    @Override
    public UserResponse updateUser(UUID id, UserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Organization organization = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setDesignation(request.getDesignation());
        user.setRole(request.getRole());
        user.setStatus(request.getStatus());
        user.setOrganization(organization);

        User updatedUser = userRepository.save(user);

        return modelMapper.map(updatedUser, UserResponse.class);
    }

    @Override
    public void deleteUser(UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.delete(user);
    }


//    @Override
//    public LoginResponse login(LoginRequest request) {
//
//        User user = userRepository.findByEmail(request.getEmail())
//                .orElseThrow(() ->
//                        new RuntimeException("Invalid email or password"));
//
//        if (!user.getPassword().equals(request.getPassword())) {
//            throw new RuntimeException("Invalid email or password");
//        }
//
//        if (!user.isEnabled()) {
//            throw new RuntimeException("User account is disabled");
//        }
//
//        UserResponse userResponse = modelMapper.map(user, UserResponse.class);
//
//        LoginResponse response = new LoginResponse();
//
//        response.setUser(userResponse);
//
//        response.setOrganizationId(user.getOrganization().getId());
//
//        // If the logged-in user is a Super Admin / Manager
//        if (user.getRole().name().equals("SUPER_ADMIN")) {
//
//            response.setManagerId(user.getId());
//
//        } else {
//
//            List<ProjectMember> members =
//                    projectMemberRepository.findByUserId(user.getId());
//
//            if (!members.isEmpty()) {
//
//                ProjectMember member = members.get(0);
//
//                response.setProjectId(member.getProject().getId());
//
//                response.setManagerId(
//                        member.getProject()
//                                .getCreatedBy()
//                                .getId()
//                );
//            }
//        }
//
//        return response;
//    }
}