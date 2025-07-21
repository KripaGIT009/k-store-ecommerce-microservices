package com.kstore.user.service.impl;

import com.kstore.common.exception.BusinessException;
import com.kstore.common.exception.ResourceNotFoundException;
import com.kstore.user.dto.UserRegistrationRequest;
import com.kstore.user.dto.UserLoginRequest;
import com.kstore.user.dto.UserResponse;
import com.kstore.user.entity.User;
import com.kstore.user.repository.UserRepository;
import com.kstore.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public UserResponse register(UserRegistrationRequest request) {
        log.info("Registering new user with username: {}", request.getUsername());
        
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username already exists", "USERNAME_EXISTS");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists", "EMAIL_EXISTS");
        }
        
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .roles(Set.of(User.Role.USER))
                .build();
        
        user = userRepository.save(user);
        log.info("User registered successfully with ID: {}", user.getId());
        
        return mapToUserResponse(user);
    }

    @Override
    public String login(UserLoginRequest request) {
        log.info("Attempting login for username: {}", request.getUsername());
        
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("Invalid username or password", "INVALID_CREDENTIALS"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("Invalid username or password", "INVALID_CREDENTIALS");
        }
        
        if (!user.isEnabled()) {
            throw new BusinessException("Account is disabled", "ACCOUNT_DISABLED");
        }
        
        String token = jwtTokenProvider.generateToken(user);
        log.info("User logged in successfully: {}", request.getUsername());
        
        return token;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUserProfile() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        return mapToUserResponse(user);
    }

    @Override
    public UserResponse updateProfile(UserRegistrationRequest request) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // Check if email is being changed and already exists
        if (!user.getEmail().equals(request.getEmail()) && 
            userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists", "EMAIL_EXISTS");
        }
        
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        
        user = userRepository.save(user);
        log.info("User profile updated successfully: {}", username);
        
        return mapToUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        return mapToUserResponse(user);
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        throw new BusinessException("User not authenticated", "NOT_AUTHENTICATED");
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .roles(user.getRoles())
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
