package com.kstore.user.controller;

import com.kstore.common.dto.ApiResponse;
import com.kstore.user.dto.UserRegistrationRequest;
import com.kstore.user.dto.UserLoginRequest;
import com.kstore.user.dto.UserResponse;
import com.kstore.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody UserRegistrationRequest request) {
        UserResponse userResponse = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(userResponse, "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody UserLoginRequest request) {
        String token = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success(token, "Login successful"));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile() {
        UserResponse userResponse = userService.getCurrentUserProfile();
        return ResponseEntity.ok(ApiResponse.success(userResponse));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(@Valid @RequestBody UserRegistrationRequest request) {
        UserResponse userResponse = userService.updateProfile(request);
        return ResponseEntity.ok(ApiResponse.success(userResponse, "Profile updated successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse userResponse = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(userResponse));
    }
}
