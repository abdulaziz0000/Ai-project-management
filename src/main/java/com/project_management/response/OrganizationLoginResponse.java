package com.project_management.response;

import lombok.Data;

import java.util.UUID;

@Data
public class OrganizationLoginResponse {

    private UUID id;

    private String name;

}