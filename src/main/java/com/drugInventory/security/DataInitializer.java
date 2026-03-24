package com.drugInventory.security;

import com.drugInventory.model.User;
import com.drugInventory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Startup Data Initializer
 * This component runs identically across any machine or deployment environment right after Spring Boot starts up.
 * Purpose: It dynamically intercepts the default static SQL hash from data.sql (which fails strict bcrypt validation 
 * on some Windows environments) and overwrites it using the platform's native valid PasswordEncoder hash.
 * This guarantees the pre-loaded admin and pharmacist demo accounts work out-of-the-box everywhere!
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Find all users currently inserted by the database seeder (data.sql)
        List<User> users = userRepository.findAll();
        
        // The exact literal bad hash seeded by data.sql that causes login rejections
        final String badHash = "$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6/0AT7.bVfSAiM4m";
        
        for (User user : users) {
             // If this account has the static bad hash, re-hash it perfectly using the current environment!
             if (badHash.equals(user.getPassword())) {
                 user.setPassword(passwordEncoder.encode("admin123"));
                 userRepository.save(user);
                 System.out.println("✅ Security Patch: Successfully re-hashed default password for " + user.getUsername());
             }
        }
    }
}
