package com.financeproject.dto;

import lombok.Data;

/**
 * Data Transfer Object (DTO) for user registration.
 * Carries the initial sign-up data from client to server.
 */
@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
}