package com.rtvnewsnetwork.reel.dto;

import com.rtvnewsnetwork.config.model.UploadedFile;
import com.rtvnewsnetwork.post.model.Video;
import com.rtvnewsnetwork.reel.model.ReelInsightModel;
import com.rtvnewsnetwork.reel.model.ReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.rtvnewsnetwork.config.model.UploadedFile.RelativePath;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReelResponseDTO {
    private String id;
    private String userId;
    private String username;   // <-- Added
    private String title;
    private Video video;
    private ReelInsightModel insights;
    private Boolean isApproved;
    private String uploadedBy;
    private ReviewStatus status;
    private String rejectionReason;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean isLiked;
    private String phoneNumber;
    private RelativePath profileImage;
}
