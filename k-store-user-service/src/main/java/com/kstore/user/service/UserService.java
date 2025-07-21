package com.kstore.user.service;

import com.kstore.user.dto.UserRegistrationRequest;
import com.kstore.user.dto.UserLoginRequest;
import com.kstore.user.dto.UserResponse;

public interface UserService {
    
    UserResponse register(UserRegistrationRequest request);
    
    String login(UserLoginRequest request);
    
    UserResponse getCurrentUserProfile();
    
    UserResponse updateProfile(UserRegistrationRequest request);
    
    UserResponse getUserById(Long id);
}
