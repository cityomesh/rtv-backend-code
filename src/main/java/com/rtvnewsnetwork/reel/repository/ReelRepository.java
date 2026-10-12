package com.rtvnewsnetwork.reel.repository;

import com.rtvnewsnetwork.reel.model.ReelModel;
import com.rtvnewsnetwork.reel.model.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ReelRepository extends MongoRepository<ReelModel, String> {

        @Query("{ 'status': ?0, '$or': [ { 'isApproved': { $ne: false } }, { 'isApproved': null } ] }")
        Page<ReelModel> findByStatusAndIsApprovedNotFalse(ReviewStatus status, Pageable pageable);

        Page<ReelModel> findByUserIdAndStatus(String userId, ReviewStatus status, Pageable pageable);

    List<ReelModel> findByUserId(String userId,Pageable pageable);
    @Query("{ 'title': { $regex: ?0, $options: 'i' } }")
    Page<ReelModel> searchByTitle(String query, Pageable pageable);
    Page<ReelModel> findByStatus(ReviewStatus status, Pageable pageable);
    @Query("{ 'title': { $regex: ?0, $options: 'i' }, 'status': ?1 }")
    Page<ReelModel> searchByTitleAndStatus(String query, ReviewStatus status, Pageable pageable);



}

