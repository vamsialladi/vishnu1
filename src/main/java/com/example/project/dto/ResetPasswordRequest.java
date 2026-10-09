package com.example.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetPasswordRequest {

    @NotBlank(message = "Token or email is required")
    private String tokenOrEmail;

    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String newPassword;

    public ResetPasswordRequest() {}

    public ResetPasswordRequest(String tokenOrEmail, String newPassword) {
        this.tokenOrEmail = tokenOrEmail;
        this.newPassword = newPassword;
    }

    public String getTokenOrEmail() {
        return tokenOrEmail;
    }

    public void setTokenOrEmail(String tokenOrEmail) {
        this.tokenOrEmail = tokenOrEmail;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
