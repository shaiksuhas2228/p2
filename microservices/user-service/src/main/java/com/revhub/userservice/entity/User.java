package com.revhub.userservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String firstName;
    private String lastName;

    @Lob
    private String profilePicture;
    private String bio;
    
    @Column(nullable = false)
    private Boolean isPrivate = false;
    
    @Column(nullable = false)
    private Boolean isVerified = false;
    
    private String verificationToken;
    private String otp;
    private LocalDateTime otpExpiry;

    @CreationTimestamp
    private LocalDateTime createdDate;
}