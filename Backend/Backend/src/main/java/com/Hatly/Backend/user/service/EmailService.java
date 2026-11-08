package com.Hatly.Backend.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String fromEmail;
    public void sendEmail(String email, String otp) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(fromEmail);
        mailMessage.setTo(email);
        mailMessage.setSubject("Hatly App - Password Reset OTP");
        mailMessage.setText("Hello,\n\n"
                + "You requested to reset your password. Your OTP code is:\n\n"
                + "👉 " + otp + " 👈\n\n"
                + "This code is valid for 15 minutes. Please do not share it with anyone.\n\n"
                + "Best regards,\n"
                + "Hatly Team");


        mailSender.send(mailMessage);

    }
}
