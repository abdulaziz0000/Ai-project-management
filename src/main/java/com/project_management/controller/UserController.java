package com.project_management.controller;

import com.project_management.request.UserRequest;
import com.project_management.response.UserResponse;
import com.project_management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable UUID id) {
        return userService.getUser(id);
    }

    @GetMapping("/organization/{organizationId}")
    public List<UserResponse> getUsersByOrganization(@PathVariable UUID organizationId) {
        return userService.getUsersByOrganization(organizationId);
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable UUID id,
                                   @RequestBody UserRequest request) {
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
    }

//    @PostMapping("/login")
//    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
//
//        return ResponseEntity.ok(userService.login(request));
//    }
}
