    package com.example.SpringBootSecurity.controller;


    import com.example.SpringBootSecurity.model.User;
    import com.example.SpringBootSecurity.security.AuthFilter;
    import com.example.SpringBootSecurity.security.AuthLogin;
    import com.example.SpringBootSecurity.service.JwtService;
    import com.example.SpringBootSecurity.service.UserService;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.userdetails.UsernameNotFoundException;
    import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/auth")
    public class SecurityController {
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;
        private final UserService userService;

        public SecurityController(JwtService jwtService, AuthenticationManager authenticationManager, UserService userService) {
            this.jwtService = jwtService;
            this.authenticationManager = authenticationManager;
            this.userService = userService;
        }

        @PostMapping("/generate")
        public String generateToken(@RequestBody AuthLogin authLogin){
                Authentication authenticate =  authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(authLogin.getUsername(),authLogin.getPassword()));
                    if(authenticate.isAuthenticated()){
                            return jwtService.generateToken(authLogin.getUsername());
                    }
                    else throw new UsernameNotFoundException("Invalid username : "+authLogin.getUsername());
        }

        @PostMapping("/create")
        public User createUser(@RequestBody User user){
             return userService.createUser(user);
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
