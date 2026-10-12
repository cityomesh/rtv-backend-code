package com.rtvnewsnetwork.notification.repository;

import com.rtvnewsnetwork.notification.model.FcmModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface FcmRepository extends MongoRepository<FcmModel, String>,FcmRepositoryCustom {


    FcmModel deleteByToken(String token);

    FcmModel findByDeviceId(String deviceId);

    Optional<FcmModel> findByToken(String token);

    @Query(value = "{}", fields = "{ 'token': 1 }")
    List<FcmModel> findAllTokensOnly();

}
