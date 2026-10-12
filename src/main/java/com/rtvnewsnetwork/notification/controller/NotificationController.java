package com.rtvnewsnetwork.notification.controller;


import com.rtvnewsnetwork.common.service.AuthDetailsHelper;
import com.rtvnewsnetwork.notification.model.FcmModel;
import com.rtvnewsnetwork.notification.model.NotificationModel;
import com.rtvnewsnetwork.notification.service.NotificationService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class NotificationController implements AuthDetailsHelper {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/fcmtoken")
    public void addFcmToken(@RequestBody FcmModel fcmModel) {
        fcmModel.setUserId(getUserId());
        notificationService.addFcmToken(fcmModel);
    }

    @PostMapping("/notification")
    public void addCustomNotification(@RequestBody NotificationModel notificationModel) {
        notificationService.sendCustomNotification(notificationModel);
    }

    @GetMapping("/notification")
    public List<NotificationModel> getAllNotification(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return notificationService.getAllNotification(pageable);
    }

}