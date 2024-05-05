package com.dance.mo.auth.Controller;

import com.dance.mo.Config.JwtService;
import com.dance.mo.Exceptions.UserException;
import com.dance.mo.Services.UserService;
import com.dance.mo.auth.DTO.*;
import com.dance.mo.auth.Service.AuthenticationService;
import com.dance.mo.auth.Service.RedisService;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.util.*;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class AuthenticationController {
    @Value("${jwt.secret}")
    private  String SECRET_KEY;
    private static final long DEFAULT_EXPIRATION_TIME_MILLIS = 604800000;
    private final AuthenticationService service;

    private final UserService userService;
    private final JwtService jwtService;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;
    public static Set<String> onlineUsers = new HashSet<>();
    private static final String CONFIRMATION_URL = "http://localhost:4200/forgot-password/%s";
    ///  endpoint : authenticate an existing user


    private String activationUrl="http://localhost:4200/activate-password";


    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        SecurityContextHolder.clearContext();
        request.getSession().invalidate();
        onlineUsers.remove(email);
        return ResponseEntity.status(HttpStatus.OK).body("Logged out successfully");
    }
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //////////

//////////




    @GetMapping("/onlineUsers")
    public Set<String> getOnlineUsers() {
        return onlineUsers;
    }


    @PostMapping("/auth")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        try {
            AuthenticationResponse response = service.authenticate(request);
            onlineUsers.add(response.getEmail());
            return ResponseEntity.ok(response);
        }
        catch (UserException e) {
            if (e.getMessage().equals("User not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        AuthenticationResponse.builder()
                                .messageResponse("User not found")
                                .build());
            } else if (e.getMessage().equals("User account is not active. Please confirm your email.")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        AuthenticationResponse.builder()
                                .messageResponse("User account is not active. Please confirm your email.")
                                .build());
            } else {
                // Handle any other UserException
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        AuthenticationResponse.builder()
                                .messageResponse("An error occurred during authentication")
                                .build());
            }
        }
    }
}



