package com.example.SpringBootSecurity.service;


import com.example.SpringBootSecurity.model.User;
import com.example.SpringBootSecurity.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    private final JwtService jwtService;


    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository, JwtService jwtService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
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
