package com.rtvnewsnetwork.otp.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document("otp_requests")
@AllArgsConstructor
@NoArgsConstructor
public class OtpModel {
    @Id
    private String id;
    private String phoneNumber;
    private String otp;
    private Instant createdAt = Instant.now();
    private Instant expiresAt;
    private Instant lastSentAt;
    private boolean isUsed = false;
    private int failedAttempts = 0;
}