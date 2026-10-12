package com.rtvnewsnetwork.notification.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

import java.util.ArrayList;
import java.util.List;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FcmModel {

    @Id
    private String id;
    private String deviceId;
    private String token;
    private Integer failureCounter = 0;
//    private List<String> failureReasons = new ArrayList<>();
    private String userId = null;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
    private Integer successCount = 0;
//    private List<String> successReasons = new ArrayList<>();
    private Instant successLastUpdatedAt;
    private Instant lastFailureAt;

}
