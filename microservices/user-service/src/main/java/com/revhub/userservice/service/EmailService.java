package com.revhub.userservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Async
    public void sendOTP(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("revhub.noreply@gmail.com");
            message.setTo(toEmail);
            message.setSubject("RevHub - Email Verification OTP");
            message.setText("Hello,\n\nYour OTP for email verification is: " + otp + "\n\nThis OTP will expire in 10 minutes.\n\nIf you did not request this, please ignore this email.\n\nThank you,\nRevHub Team");
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("Email sending failed. OTP: " + otp);
        }
    }
    
    @Async
    public void sendPasswordResetOTP(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("revhub.noreply@gmail.com");
            message.setTo(toEmail);
            message.setSubject("RevHub - Password Reset OTP");
            message.setText("Hello,\n\nYour OTP for password reset is: " + otp + "\n\nThis OTP will expire in 10 minutes.\n\nIf you did not request this, please ignore this email.\n\nThank you,\nRevHub Team");
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("Password reset email failed. OTP: " + otp);
        }
    }
}
