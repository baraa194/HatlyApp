package com.Hatly.Backend.user.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_resets",indexes = {
        @Index(name="idx_password_resets_user_id",columnList ="user_id")
})
@Data
public class PasswordReset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
   @Column(nullable = false)
    private String otpHash;

    private LocalDateTime expiresAt;

    private LocalDateTime createdAt;

    private LocalDateTime consumedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}