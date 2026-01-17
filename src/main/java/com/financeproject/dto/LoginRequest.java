package com.financeproject.dto;

import lombok.Data;

/**
 * Data Transfer Object (DTO) for handling user login requests.
 */
@Data
public class LoginRequest {
    private String email;
    private String password;
}