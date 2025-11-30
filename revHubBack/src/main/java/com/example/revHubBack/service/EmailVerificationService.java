package com.example.revHubBack.service;

import com.example.revHubBack.entity.User;
import com.example.revHubBack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EmailVerificationService {
    @Autowired
    private UserRepository userRepository;
    
    public String sendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getIsVerified()) {
            return "User already verified";
        }
        
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        userRepository.save(user);
        
        System.out.println("Verification token for " + email + ": " + token);
        return "Verification token: " + token;
    }

    public String verifyEmail(String token) {
        User user = userRepository.findAll().stream()
                .filter(u -> token.equals(u.getVerificationToken()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));
        
        user.setIsVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);
        
        return "Email verified successfully";
    }
}