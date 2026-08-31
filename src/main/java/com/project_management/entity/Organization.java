package com.project_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "organizations")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String industry;

    @Column(unique = true)
    private String email;

    private String phone;

    private String address;


    private UUID ownerId;   // SUPER_ADMIN user
    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "organization")
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "organization")
    private List<Project> projects = new ArrayList<>();
}