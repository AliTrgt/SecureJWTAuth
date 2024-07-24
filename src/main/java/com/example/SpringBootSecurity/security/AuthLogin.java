package com.example.SpringBootSecurity.security;


import lombok.Getter;
import lombok.Setter;

public record AuthRequest
    (
        String username;
        String password;
    
) {}
