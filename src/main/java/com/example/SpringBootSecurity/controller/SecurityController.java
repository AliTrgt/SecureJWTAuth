package com.example.JwtUtil.controller;


import com.example.JwtUtil.request.AuthRequest;
import com.example.JwtUtil.service.JwtService;
import com.example.JwtUtil.service.SecurityUserService;
import com.example.JwtUtil.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class SecurityController {

        private final AuthenticationManager authenticationManager;
        private final JwtService jwtService;

        private final UserService service;

        private final SecurityUserService userService;

    public SecurityController(AuthenticationManager authenticationManager, JwtService jwtService, UserService service, SecurityUserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.service = service;
        this.userService = userService;
    }


    @PostMapping("/generateToken")
    public String generateToken(@RequestBody AuthRequest authRequest){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.username(),authRequest.password()));
        if(authentication.isAuthenticated()){
                             return jwtService.generateAccessToken(authRequest.username());
        }
        else
            throw new UsernameNotFoundException("username not found : "+authRequest.username());

    }


    @PostMapping("/login")
    public Map<String,String> login(@RequestBody AuthRequest authRequest){
                return  service.login(authRequest);

    }


    @PostMapping("/refresh")
    public Map<String,String> refresh(@RequestBody Map<String,String> request){
                return service.refresh(request);
    }



    @GetMapping("/user")
    public String getUser(){
        return "Hi User";
    }

    @GetMapping("/manager")
    public String getManager(){
        return "Hi Manager";
    }

    @GetMapping("/admin")
    public String getAdmin(){
        return  "Hi Admin";
    }










}
