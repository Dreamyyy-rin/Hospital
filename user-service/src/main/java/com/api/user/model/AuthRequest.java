package com.api.user.model;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}
