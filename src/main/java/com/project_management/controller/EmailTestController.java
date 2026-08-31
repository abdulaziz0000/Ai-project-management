package com.project_management.controller;

import com.project_management.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmailTestController {

    private final EmailService emailService;

    @GetMapping("/api/test/email")
    public String sendTestEmail() {

        emailService.sendEmail(
                "azizabdul04327@gmail.com", // Replace with the email you want to receive the test
                "Spring Boot Email Test",
                "Congratulations! Your email configuration is working successfully."
        );

        return "Email sent successfully.";
    }
}