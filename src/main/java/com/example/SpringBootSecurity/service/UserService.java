package com.example.JwtUtil.service;


import com.example.JwtUtil.model.Role;
import com.example.JwtUtil.model.User;
import com.example.JwtUtil.repository.UserRepository;
import com.example.JwtUtil.request.AuthRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;

        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public User createUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Map<String,String> refresh(Map<String,String> request){
        String refreshToken = request.get("refreshToken");
        String username = jwtService.extractUser(refreshToken);
        UserDetails userDetails = userRepository.findByUsername(username);
        if(jwtService.validateToken(refreshToken,userDetails)){
            String newAccessToken = jwtService.generateAccessToken(username);
            Map<String,String> tokens = new HashMap<>();
            tokens.put("accessToken",newAccessToken);
            return  tokens;
        }
        throw new UsernameNotFoundException("User Not Found Exception : "+username);
    }
    public Map<String,String> login(AuthRequest request){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(),request.password()));
        if(authentication.isAuthenticated()){
            String accessToken = jwtService.generateAccessToken(request.username());
            String refreshToken = jwtService.generateRefreshToken(request.password());
            Map<String,String> tokens = new HashMap<>();
            tokens.put("accessToken",accessToken);
            tokens.put("refreshToken",refreshToken);
            return tokens;
        }
        throw new UsernameNotFoundException("Invalid Username :"+request.username());
    }
}
