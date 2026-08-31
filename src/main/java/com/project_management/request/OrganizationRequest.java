package com.project_management.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationRequest {

    // Organization
    private String name;
    private String industry;
    private String email;
    private String phone;
    private String address;

    // Manager
    private String firstName;
    private String lastName;
    private String managerEmail;
    private String password;
    private String managerPhone;
}
