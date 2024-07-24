package com.example.SpringBootSecurity.config;

import com.example.SpringBootSecurity.security.AuthFilter;
import com.example.SpringBootSecurity.service.JwtService;
import com.example.SpringBootSecurity.service.SecurityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {
    private final AuthFilter authFilter;
    private final SecurityService userService;

    public SecurityConfig(AuthFilter authFilter, SecurityService userService) {
        this.authFilter = authFilter;
        this.userService = userService;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
            return http
                    .headers(headers -> headers.frameOptions().disable())
                    .sessionManagement(x -> x.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .csrf(AbstractHttpConfigurer::disable)
                    .formLogin(AbstractHttpConfigurer::disable)
                    .cors(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth ->
                                auth
                                        .requestMatchers("/auth/user").hasRole("USER")
                                        .requestMatchers("/auth/manager").hasRole("MANAGER")
                                        .requestMatchers("/auth/admin").hasRole("ADMIN")
                                        .anyRequest().permitAll()
                            )
                    .authenticationProvider(authenticationProvider())
                    .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                    .build();
    }
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userService);
        return daoAuthenticationProvider;
    }

}

