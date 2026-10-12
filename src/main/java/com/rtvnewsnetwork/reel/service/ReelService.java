package com.rtvnewsnetwork.reel.service;

import com.rtvnewsnetwork.comment.model.CommentModel;
import com.rtvnewsnetwork.reel.dto.ReelResponseDTO;
import com.rtvnewsnetwork.reel.model.ReelInsightModel;
import com.rtvnewsnetwork.reel.model.ReelModel;
import com.rtvnewsnetwork.reel.model.RejectionReasonDto;
import com.rtvnewsnetwork.reel.model.ReviewStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReelService {
    ReelModel addReel(String userId,ReelModel reelModel);
    ReelModel updateReel(String reelId, ReelModel reelModel) throws Exception;
    ReelModel deleteReel(String reelId);
//    List<ReelModel> getAllReels(ReviewStatus status,Pageable pageable);
    ReelModel getReelById(String reelId);
    ReelModel incrementLikes(String reelId,String userId);
    ReelModel decrementLikes(String reelId,String userId);
    CommentModel addComment(String reelId,String userId,String userName, CommentModel commentModel);
    CommentModel addReply(String reelId, String parentId, CommentModel reply);
    List<CommentModel> getCommentsByReelId(String reelId, Pageable pageable);
    ReelInsightModel deleteComment(String reelId, String commentId);
    CommentModel editComment(String reelId, String commentId, String updatedComment);
    ReelModel addReelByAdmin(ReelModel reelModel) throws     Exception;
    ReelModel approveReel(String reelId, Boolean isApproved, RejectionReasonDto rejectionReason);
    List<ReelModel> getAllReels(String userId, Pageable pageable);
    List<ReelModel> getAllReelsByUserId(String userId, ReviewStatus status, Pageable pageable);
    ReelModel ReUploadReelByUserId(String userId, ReelModel reelModel);
    List<ReelModel> getAllReelsByUserId(String userId, Pageable pageable);
    List<ReelResponseDTO> getAllReels(ReviewStatus status, Pageable pageable);
    List<ReelResponseDTO> getAllReelsSearch(ReviewStatus status, String query, Pageable pageable);
}
