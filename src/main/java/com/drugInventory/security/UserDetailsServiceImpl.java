package com.drugInventory.security;

import com.drugInventory.model.User;
import com.drugInventory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Custom UserDetailsService implementation.
 * Spring Security calls this to load user details during authentication.
 * We load the user from our MySQL database instead of in-memory.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Loads user from DB by username.
     * Spring Security uses the returned UserDetails to validate credentials.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Find user in database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username: " + username));

        // Check if user is active
        if (!user.isActive()) {
            throw new UsernameNotFoundException("User account is disabled: " + username);
        }

        // Convert our User entity to Spring Security's UserDetails format
        // The role is prefixed with "ROLE_" as Spring Security convention
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())  // already bcrypt-hashed
                .authorities(Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
                .build();
    }
}
