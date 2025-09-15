package com.irctc.controller;

import com.irctc.model.User;
import com.irctc.config.*;
import com.irctc.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.Authentication;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody User user) {
        try {
            User registeredUser = userService.register(user);
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
            Map<String, Object> response = new HashMap<>();
            response.put("id", registeredUser.getId());
            response.put("username", registeredUser.getUsername());
            response.put("role", registeredUser.getRole());
            response.put("email", registeredUser.getEmail());
            response.put("fullname", registeredUser.getFullname());
            response.put("phone", registeredUser.getPhoneNumber());
            response.put("gender", registeredUser.getGender());
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/api/auth/me")
    public ResponseEntity<?> me(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        return ResponseEntity.ok(Map.of(
            "username", auth.getName(),
            "roles", auth.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.toList())
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");
            if (email == null || password == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email and password are required"));
            }
            User user = userService.login(email, password);
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("role", user.getRole());
            response.put("email", user.getEmail());
            response.put("fullname", user.getFullname());
            response.put("phone", user.getPhoneNumber());
            response.put("gender", user.getGender());
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }
}