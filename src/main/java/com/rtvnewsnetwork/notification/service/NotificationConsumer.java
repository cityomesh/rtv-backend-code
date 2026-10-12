package com.rtvnewsnetwork.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rtvnewsnetwork.event.model.EventModel;
import com.rtvnewsnetwork.notification.model.NotificationEventType;
import com.rtvnewsnetwork.notification.model.NotificationModel;
import com.rtvnewsnetwork.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.kafka.support.Acknowledgment;


import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class NotificationConsumer {
    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);


    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    public NotificationConsumer(
            NotificationService notificationService,
            NotificationRepository notificationRepository
       ) {
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
    }

        @KafkaListener(
            topics = "${event.kafka.topic.notifications}",
            groupId = "${rtv.dev.notification.kafka.groupid}"
    )
        public void processNotificationEvent(EventModel eventModel) {
            log.info("📥 [Kafka] Received notification event: {}", eventModel);
            try {
                NotificationModel payload = createPayload(eventModel);
                NotificationEventType notificationType =
                        NotificationEventType.getNotificationType(eventModel.getEventType());

                log.info("🔍 NotificationType resolved: {}", notificationType);

                switch (notificationType) {
                    case ALL_USER:
                        log.info("Sending to ALL users");
                        notificationService.sendNotification(payload);
                        break;

                    case SINGLE_USER:
                        log.info("Sending to SINGLE user: {}", eventModel.getUserId());
                        notificationService.sendNotification(eventModel.getUserId(), payload);
                        break;

                    case GROUP_USER:
                        log.info("Sending to GROUP users");
                        notificationService.sendNotification(extractUserList(eventModel), payload);
                        break;

                    default:
                        log.warn("Unknown notification type: {}", eventModel.getEventType());
                        break;
                }

                log.info("✅ Notification processed successfully for event: {}", eventModel.getEventId());

            } catch (Exception e) {
                log.error("🔥 Error processing Kafka message (eventId={}): {}",
                        eventModel.getEventId(), e.getMessage(), e);
            }
        }

    private NotificationModel createPayload(EventModel eventModel) {
        Map<String, Object> data = (Map<String, Object>) eventModel.getData();
        if (data == null) data = Collections.emptyMap();

        String title = (String) data.getOrDefault("title", "");
        String description = (String) data.getOrDefault("description", "");
        String imageUrl = (String) data.get("imageUrl");
        String logoUrl = "https://d3l8l5q21enszy.cloudfront.net/images/TRZyPzQTzObWqcWZRhbfZHFSnrTEwsHC.png";
        String path = (String) data.get("path");
//
//        NotificationModel notificationModel = new NotificationModel(
//                title,
//                description,
//                imageUrl,
//                logoUrl,
//                path,
//                eventModel.getCreatedAt() != null ? eventModel.getCreatedAt() : Instant.now()
//        );
        NotificationModel notificationModel = new NotificationModel();
        notificationModel.setTitle(title);
        notificationModel.setDescription(description);
        notificationModel.setImageUrl(imageUrl);
        notificationModel.setLogoUrl(logoUrl);
        notificationModel.setPath(path);
        System.out.println("notificationModel: "+notificationModel);
        return notificationModel;
    }

    private List<String> extractUserList(EventModel eventModel) {
        Map<String, Object> data = (Map<String, Object>) eventModel.getData();
        if (data == null) return Collections.emptyList();
        return (List<String>) data.getOrDefault("userlist", Collections.emptyList());
    }
}
