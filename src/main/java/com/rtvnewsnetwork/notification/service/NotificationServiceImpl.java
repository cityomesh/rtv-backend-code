package com.rtvnewsnetwork.notification.service;

import com.google.firebase.messaging.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rtvnewsnetwork.common.exception.ResourceNotFoundException;
import com.rtvnewsnetwork.config.kafka.KafkaTopicConfig;
import com.rtvnewsnetwork.event.model.EventType;
import com.rtvnewsnetwork.event.service.EventPublisher;
import com.rtvnewsnetwork.event.service.EventService;
import com.rtvnewsnetwork.notification.model.FcmModel;
import com.rtvnewsnetwork.notification.model.NotificationModel;
import com.rtvnewsnetwork.notification.repository.FcmRepository;
import com.rtvnewsnetwork.notification.repository.NotificationRepository;
import com.rtvnewsnetwork.user.model.User;
import com.rtvnewsnetwork.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService, EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);


    private final UserRepository userRepository;
    private final FcmRepository fcmRepository;
    private final NotificationRepository notificationRepository;
    private final EventService eventService;

    public NotificationServiceImpl(UserRepository userRepository,
                                   FcmRepository fcmRepository,
                                   EventService eventService,
                                   NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.fcmRepository = fcmRepository;
        this.eventService = eventService;
        this.notificationRepository = notificationRepository;

    }

    @Override
    public void sendNotification(String userId, NotificationModel payload) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<String> tokenList = new ArrayList<>(user.getFcmToken().values());

        Notification notification = Notification.builder()
                .setTitle(payload.getTitle())
                .setBody(payload.getDescription())
                .setImage(payload.getImageUrl())
                .build();

        MulticastMessage message = MulticastMessage.builder()
                .setNotification(notification)
                .putData("logo", String.valueOf(payload.getLogoUrl()))
                .putData("path", payload.getPath())
                .addAllTokens(tokenList)
                .build();

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
            System.out.println("Successfully sent message to " + response.getSuccessCount() + " devices");
            if (response.getFailureCount() > 0) {
                System.out.println("Failed to send message to " + response.getFailureCount() + " devices");
                for (int i = 0; i < response.getResponses().size(); i++) {
                    SendResponse sendResponse = response.getResponses().get(i);
                    if (!sendResponse.isSuccessful()) {
                        System.out.println("Failed token: " + tokenList.get(i) + ", Error: " + sendResponse.getException());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendNotification(NotificationModel payload) {
        log.info("Saving notification: {}", payload);
        notificationRepository.save(payload);

        // Fetch only token field for minimal DB load
        List<String> tokenList = fcmRepository.findAllTokensOnly().stream()
                .map(FcmModel::getToken)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (tokenList.isEmpty()) {
            log.warn("No FCM tokens found — skipping notification send.");
            return;
        }

        log.info("Total tokens fetched: {}", tokenList.size());

        Notification notification = Notification.builder()
                .setTitle(payload.getTitle())
                .setBody(payload.getDescription())
                .setImage(payload.getImageUrl())
                .build();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        int batchSize = 450;
        int totalSuccessCount = 0;
        int totalFailureCount = 0;
        int totalBatches = (int) Math.ceil((double) tokenList.size() / batchSize);
        List<String> allFailureTokens = new ArrayList<>();


        for (int i = 0; i < tokenList.size(); i += batchSize) {
            int end = Math.min(i + batchSize, tokenList.size());
            List<String> batch = tokenList.subList(i, end);

            MulticastMessage message = MulticastMessage.builder()
                    .setNotification(notification)
                    .putData("path", payload.getPath())
                    .addAllTokens(batch)
                    .build();

            log.info("Sending batch {}-{} ({} tokens)", i + 1, end, batch.size());
            log.debug("FCM Message: {}", gson.toJson(message));

            try {
                BatchResponse resp = FirebaseMessaging.getInstance().sendEachForMulticast(message);
                log.info("FCM Batch Response: Success={}, Failures={}", resp.getSuccessCount(), resp.getFailureCount());

                int batchSuccess = resp.getSuccessCount();
                int batchFailure = resp.getFailureCount();
                log.info("Batch {}/{} Result: Success={}, Failures={}, TokensSent={}",
                        (i / batchSize) + 1, totalBatches, batchSuccess, batchFailure, batch.size());

                totalSuccessCount += batchSuccess;
                totalFailureCount += batchFailure;

                for (int j = 0; j < resp.getResponses().size(); j++) {
                    SendResponse r = resp.getResponses().get(j);
                    String token = batch.get(j);

                    if (r.isSuccessful()) {
                        handleSuccess(token);
                    } else {
                        allFailureTokens.add(token);
                        handleFailure(token, r);
                    }
                }
            } catch (Exception e) {
                log.error("Error sending batch {}-{}: {}", i + 1, end, e.getMessage(), e);
            }
        }

        log.info("All batches processed successfully for notification: {}", payload.getTitle());
        log.info("Overall Summary -> Total Success: {}, Total Failures: {}, Total Tokens: {}",
                totalSuccessCount, totalFailureCount, tokenList.size());
        log.warn("All Failed Tokens ({}): {}", allFailureTokens.size(), allFailureTokens);

    }

    private void handleSuccess(String token) {
        try {
            fcmRepository.updateSuccessCount(token);
            log.info("Success updated for token {}", token);
        } catch (Exception e) {
            log.error("Error updating success count for token {}: {}", token, e.getMessage());
        }
    }

    private void handleFailure(String token, SendResponse response) {
//        MessagingErrorCode code = response.getException() != null
//                ? response.getException().getMessagingErrorCode()
//                : null;
//        String reason = response.getException() != null
//                ? response.getException().getMessage()
//                : "Unknown error";
//
//        String formattedTime = java.time.format.DateTimeFormatter.ISO_INSTANT.format(java.time.Instant.now());

        try {
            fcmRepository.updateFailureCount(token);
            log.warn("Failure updated for token {}", token);
        } catch (Exception e) {
            log.error("Error updating failure for token {}: {}", token, e.getMessage());
        }
    }




    @Override
    public void sendNotification(List<String> userList, NotificationModel payload) {
        List<String> tokenList = getAllTokensByUserIds(userList);

        Notification notification = Notification.builder()
                .setTitle(removeHtml(payload.getTitle()))
                .setBody(payload.getDescription() != null ? removeHtml(payload.getDescription()) : null)
                .setImage(payload.getImageUrl())
                .build();

        MulticastMessage message = MulticastMessage.builder()
                .setNotification(notification)
                .putData("path", payload.getPath())
                .addAllTokens(tokenList)
                .build();

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
            System.out.println("Successfully sent message to " + response.getSuccessCount() + " devices");
            if (response.getFailureCount() > 0) {
                System.out.println("Failed to send message to " + response.getFailureCount() + " devices");
                for (int i = 0; i < response.getResponses().size(); i++) {
                    SendResponse sendResponse = response.getResponses().get(i);
                    if (!sendResponse.isSuccessful()) {
                        System.out.println("Failed token: " + tokenList.get(i) + ", Error: " + sendResponse.getException());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addFcmToken(FcmModel fcmModel) {
        FcmModel existingFcm = fcmRepository.findByDeviceId(fcmModel.getDeviceId());
        if (existingFcm != null) {
            existingFcm.setToken(fcmModel.getToken());
            if (Objects.nonNull(fcmModel.getUserId())) existingFcm.setUserId(fcmModel.getUserId());
            existingFcm.setUpdatedAt(Instant.now());
            fcmRepository.save(existingFcm);
        } else {
            fcmRepository.save(fcmModel);
        }
    }

    @Override
    public void sendCustomNotification(NotificationModel notificationDto) {
        Map<String, String> eventData = new HashMap<>();
        eventData.put("title", notificationDto.getTitle());
        eventData.put("description", notificationDto.getDescription());

        if (notificationDto.getImageUrl() != null) {
            eventData.put("imageUrl", notificationDto.getImageUrl());
        }
        if (notificationDto.getPath() != null) {
            eventData.put("path", notificationDto.getPath());
        }

        produceEventToKafka(EventType.NEW_POST, null, KafkaTopicConfig.NOTIFICATIONS_CHANNEL, eventData);
    }

    @Override
    public List<NotificationModel> getAllNotification(Pageable pageable) {
        return notificationRepository.findAll(pageable).toList();
    }

    private String removeHtml(String input) {
        return input.replaceAll("<[^>]*>", "").trim();
    }

    private List<String> getAllTokensByUserIds(List<String> userIds) {
        List<User> users = userRepository.findAllByIdIn(userIds);
        return users.stream()
                .flatMap(user -> user.getFcmToken().values().stream())
                .collect(Collectors.toList());
    }

    @Override
    public void deleteNotifications(List<String> notificationIds) {
        notificationRepository.deleteAllById(notificationIds);
    }

    @Override
    public EventService getEventService() {
        return this.eventService;
    }
}
