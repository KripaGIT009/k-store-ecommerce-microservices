package com.kstore.user.service;

import com.kstore.user.entity.User;
import com.kstore.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);
        
        User user = userRepository.findByUsernameWithRoles(username)
            .orElseThrow(() -> {
                log.warn("User not found with username: {}", username);
                return new UsernameNotFoundException("User not found with username: " + username);
            });
        
        log.debug("User found: {} with roles: {}", user.getUsername(), user.getRoles().size());
        return user;
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", email);
        
        User user = userRepository.findByEmailWithRoles(email)
            .orElseThrow(() -> {
                log.warn("User not found with email: {}", email);
                return new UsernameNotFoundException("User not found with email: " + email);
            });
        
        log.debug("User found: {} with roles: {}", user.getEmail(), user.getRoles().size());
        return user;
    }
}
