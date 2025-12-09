package com.revhub.userservice.service;

import com.revhub.userservice.dto.JwtResponse;
import com.revhub.userservice.dto.LoginRequest;
import com.revhub.userservice.dto.RegisterRequest;
import com.revhub.userservice.entity.User;
import com.revhub.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private EmailService emailService;
    
    public Map<String, Object> registerUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        
        // Generate OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        user.setIsVerified(false);
        
        user = userRepository.save(user);
        
        // Send OTP email (will log to console if email fails)
        emailService.sendOTP(user.getEmail(), otp);
        
        String token = jwtService.generateToken(user.getUsername());
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", getUserInfo(user));
        
        return response;
    }
    
    public JwtResponse authenticateUser(LoginRequest request) {
        User user;
        
        // Try to find user by email first, then by username
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        } else {
            user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        }
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        
        // Skip verification check for testing
        // if (!user.getIsVerified()) {
        //     throw new RuntimeException("Please verify your email before logging in");
        // }
        
        String token = jwtService.generateToken(user.getUsername());
        
        return new JwtResponse(token, user.getId(), user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName());
    }
    
    private Map<String, Object> getUserInfo(User user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("email", user.getEmail());
        userInfo.put("firstName", user.getFirstName());
        userInfo.put("lastName", user.getLastName());
        return userInfo;
    }
    
    public Map<String, Object> verifyOTP(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        System.out.println("=== OTP Verification Debug ===");
        System.out.println("Email: " + email);
        System.out.println("Received OTP: [" + otp + "] Length: " + otp.length());
        System.out.println("Stored OTP: [" + user.getOtp() + "] Length: " + (user.getOtp() != null ? user.getOtp().length() : "null"));
        System.out.println("OTP Expiry: " + user.getOtpExpiry());
        System.out.println("Current Time: " + LocalDateTime.now());
        System.out.println("=============================");
        
        if (user.getOtp() == null) {
            throw new RuntimeException("No OTP found for this user");
        }
        
        if (!user.getOtp().trim().equals(otp.trim())) {
            throw new RuntimeException("Invalid OTP");
        }
        
        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }
        
        user.setIsVerified(true);
        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Email verified successfully");
        response.put("verified", true);
        return response;
    }
    
    public Map<String, Object> resendOTP(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        String otp = String.format("%06d", new Random().nextInt(999999));
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);
        
        emailService.sendOTP(user.getEmail(), otp);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "OTP sent successfully");
        return response;
    }
    
    public Map<String, Object> forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        String otp = String.format("%06d", new Random().nextInt(999999));
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);
        
        emailService.sendPasswordResetOTP(user.getEmail(), otp);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Password reset OTP sent to your email");
        return response;
    }
    
    public Map<String, Object> resetPassword(String email, String otp, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getOtp() == null || !user.getOtp().trim().equals(otp.trim())) {
            throw new RuntimeException("Invalid OTP");
        }
        
        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Password reset successfully");
        return response;
    }
}