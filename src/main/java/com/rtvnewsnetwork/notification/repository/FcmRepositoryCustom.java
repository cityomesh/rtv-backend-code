package com.rtvnewsnetwork.notification.repository;

public interface FcmRepositoryCustom {
    void updateSuccessCount(String token);
    void updateFailureCount(String token);
}
