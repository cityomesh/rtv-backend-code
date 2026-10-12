package com.rtvnewsnetwork.otp.repository;

import com.rtvnewsnetwork.otp.model.OtpModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository extends MongoRepository<OtpModel, String> {
    OtpModel findTopByPhoneNumberOrderByCreatedAtDesc(String phoneNumber);
}
