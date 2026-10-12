package com.rtvnewsnetwork.reel.service;

import ch.qos.logback.classic.model.ReceiverModel;
import com.rtvnewsnetwork.comment.model.CommentModel;
import com.rtvnewsnetwork.comment.model.TaggedUser;
import com.rtvnewsnetwork.comment.repository.CommentRepository;
import com.rtvnewsnetwork.common.model.CONTENT_TYPE;
import com.rtvnewsnetwork.common.service.ThumbnailExtractorService;
import com.rtvnewsnetwork.config.kafka.KafkaTopicConfig;
import com.rtvnewsnetwork.event.model.EventType;
import com.rtvnewsnetwork.event.service.EventPublisher;
import com.rtvnewsnetwork.event.service.EventService;
import com.rtvnewsnetwork.like.model.LikeModel;
import com.rtvnewsnetwork.like.repository.LikeRepository;
import com.rtvnewsnetwork.reel.dto.ReelResponseDTO;
import com.rtvnewsnetwork.reel.model.ReelInsightModel;
import com.rtvnewsnetwork.reel.model.ReelModel;
import com.rtvnewsnetwork.reel.model.RejectionReasonDto;
import com.rtvnewsnetwork.reel.model.ReviewStatus;
import com.rtvnewsnetwork.reel.repository.ReelRepository;
import com.rtvnewsnetwork.user.model.User;
import com.rtvnewsnetwork.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.rtvnewsnetwork.config.model.UploadedFile.RelativePath;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ReelServiceImpl implements ReelService, EventPublisher {

    private final ReelRepository reelRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final EventService eventService;
    private final ThumbnailExtractorService thumbnailExtractorService;
    public ReelServiceImpl(
            ReelRepository reelRepository,
            CommentRepository commentRepository,
            LikeRepository likeRepository,
            UserRepository userRepository,
            EventService eventService, ThumbnailExtractorService thumbnailExtractorService
    ) {
        this.reelRepository = reelRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.eventService = eventService;
        this.thumbnailExtractorService = thumbnailExtractorService;
    }

    @Override
    public ReelModel addReel(String userId,ReelModel reelModel) {
        reelModel.setUserId(userId);
        reelModel.setInsights(new ReelInsightModel(0, 0));
        reelModel.setStatus(ReviewStatus.UNDER_REVIEW);
        reelModel.setIsApproved(false);
        reelModel.setUploadedBy("user");
        return reelRepository.save(reelModel);
    }

    @Override
    public ReelModel updateReel(String reelId, ReelModel reelModel) throws Exception {
        ReelModel existing = reelRepository.findById(reelId)
                .orElseThrow(() -> new RuntimeException("Reel not found"));

        existing.setTitle(reelModel.getTitle());
//        existing.setDescription(reelModel.getDescription());
//        existing.setThumbnail(reelModel.getThumbnail());
//        existing.setVideoURL(reelModel.getVideoURL());
        if(reelModel.getVideo()!=null){
                if(reelModel.getVideo().getExternalFile()!=null){
                    if(reelModel.getVideo().getExternalFile().getUrl()!=null){
                        String platform = thumbnailExtractorService.platformFromUrl(reelModel.getVideo().getExternalFile().getUrl());
                        System.out.println("platform"+platform);
                        if (platform != null && platform.equalsIgnoreCase("youtube")) {
                            reelModel.getVideo().getExternalFile().setThumbnailUrl(getYouTubeThumbnail(reelModel.getVideo().getExternalFile().getUrl()));
                        } else if (platform != null && platform.equalsIgnoreCase("facebook")) {
                            reelModel.getVideo().getExternalFile().setThumbnailUrl(thumbnailExtractorService.extractFacebookThumbnail(reelModel.getVideo().getExternalFile().getUrl()));
                        } else if (platform != null && platform.equalsIgnoreCase("twitter")) {
                            String thumbnailUrl = thumbnailExtractorService.extractTwitterThumbnail(reelModel.getVideo().getExternalFile().getUrl());
                            if (thumbnailUrl != null) {
                                reelModel.getVideo().getExternalFile().setThumbnailUrl(thumbnailUrl);
                            } else {
                                reelModel.getVideo().getExternalFile().setThumbnailUrl("https://rtvnews-media.s3.ap-south-1.amazonaws.com/images/twitter_thumbnail.png");
                            }
                        }
                    }
                }
            existing.setVideo(reelModel.getVideo());
        }
        existing.setUploadedBy("admin");
        existing.setIsApproved(true);
        return reelRepository.save(existing);
    }

    @Override
    public ReelModel deleteReel(String reelId) {
        ReelModel existing = reelRepository.findById(reelId)
                .orElseThrow(() -> new RuntimeException("Reel not found"));
        reelRepository.delete(existing);
        return existing;
    }

//    @Override
//    public List<ReelModel> getAllReels(ReviewStatus status,Pageable pageable) {
//        if (status != null) {
//            return reelRepository.findAll(pageable)
//                    .stream()
//                    .filter(r -> status.equals(r.getStatus()))
//                    .toList();
//        }
//        return reelRepository.findAll();
//    }
@Override
public List<ReelResponseDTO> getAllReels(ReviewStatus status, Pageable pageable) {

    List<ReelModel> reels;

    if (status != null) {
        reels = reelRepository.findAll(pageable)
                .stream()
                .filter(r -> status.equals(r.getStatus()))
                .toList();
    } else {
        reels = reelRepository.findAll(pageable).toList();
    }

    return reels.stream()
            .map(reel -> {

                String username = "Unknown";
                String phoneNumber = null;
                RelativePath profileImage = null;

                if (reel.getUserId() != null && !reel.getUserId().isBlank()) {

                    User user = userRepository.findById(reel.getUserId()).orElse(null);

                    if (user != null) {
                        username = user.getName();
                        phoneNumber = user.getPhoneNumber();
                        profileImage = user.getProfileImage();
                    }
                }

                return new ReelResponseDTO(
                        reel.getId(),
                        reel.getUserId(),
                        username,
                        reel.getTitle(),
                        reel.getVideo(),
                        reel.getInsights(),
                        reel.getIsApproved(),
                        reel.getUploadedBy(),
                        reel.getStatus(),
                        reel.getRejectionReason(),
                        reel.getCreatedAt(),
                        reel.getUpdatedAt(),
                        reel.isLiked(),
                        phoneNumber,
                        profileImage

                );
            })
            .toList();
}



    @Override
    public ReelModel getReelById(String reelId) {
        return reelRepository.findById(reelId)
                .orElseThrow(() -> new RuntimeException("Reel not found"));
    }

    @Override
    public ReelModel incrementLikes(String reelId, String userId) {
        ReelModel reel = reelRepository.findById(reelId)
                .orElseThrow(() -> new RuntimeException("Reel not found"));

        reel.getInsights().setNoOfLikes(reel.getInsights().getNoOfLikes() + 1);
        ReelModel updatedReel = reelRepository.save(reel);

        likeRepository.save(new LikeModel(null, userId, reelId, CONTENT_TYPE.REEL));
        updatedReel.setLiked(true);

        return updatedReel;
    }

    @Override
    public ReelModel decrementLikes(String reelId, String userId) {
        int deletedRows = likeRepository.deleteByUserIdAndContentId(userId, reelId);
        if (deletedRows > 0) {
            ReelModel reel = reelRepository.findById(reelId)
                    .orElseThrow(() -> new RuntimeException("Reel not found"));

            if (reel.getInsights().getNoOfLikes() > 0) {
                reel.getInsights().setNoOfLikes(reel.getInsights().getNoOfLikes() - 1);
            }
            ReelModel updatedReel = reelRepository.save(reel);
            updatedReel.setLiked(false);
            return updatedReel;
        }
        return null;
    }

    @Override
    public CommentModel addComment(String reelId, String userId,String userName,CommentModel commentModel) {

        CommentModel resolvedComment = resolveTaggedUsers(commentModel);
        resolvedComment.setUserId(userId);
        resolvedComment.setUsername(userName);
        resolvedComment.setContentId(reelId);
        resolvedComment.setTargetType(CONTENT_TYPE.REEL);
        CommentModel savedComment = commentRepository.save(resolvedComment);

        ReelModel reel = reelRepository.findById(reelId)
                .orElseThrow(() -> new RuntimeException("Reel not found"));
        reel.getInsights().setNoOfComments(reel.getInsights().getNoOfComments() + 1);
        reelRepository.save(reel);

        return savedComment;
    }

    @Override
    public CommentModel addReply(String reelId, String parentId, CommentModel reply) {
        CommentModel parent = commentRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent comment not found"));

        CommentModel resolvedReply = resolveTaggedUsers(reply);
        resolvedReply.setContentId(reelId);
        resolvedReply.setTargetType(CONTENT_TYPE.REEL);

        List<CommentModel> updatedReplies = new ArrayList<>(parent.getReplies());
        updatedReplies.add(resolvedReply);
        parent.setReplies(updatedReplies);

        commentRepository.save(parent);
        return resolvedReply;
    }

    @Override
    public List<CommentModel> getCommentsByReelId(String reelId, Pageable pageable) {
        return commentRepository.findByContentIdAndTargetType(reelId, CONTENT_TYPE.REEL, pageable);
    }

    @Override
    public ReelInsightModel deleteComment(String reelId, String commentId) {
        CommentModel comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        commentRepository.deleteById(commentId);

        ReelModel reel = reelRepository.findById(reelId)
                .orElseThrow(() -> new RuntimeException("Reel not found"));
        if (reel.getInsights().getNoOfComments() > 0) {
            reel.getInsights().setNoOfComments(reel.getInsights().getNoOfComments() - 1);
        }
        reelRepository.save(reel);

        return reel.getInsights();
    }

    @Override
    public CommentModel editComment(String reelId, String commentId, String updatedComment) {
        CommentModel comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        comment.setComment(updatedComment);
        return commentRepository.save(comment);
    }

    @Override
    public ReelModel addReelByAdmin(ReelModel reelModel) throws Exception {
        reelModel.setInsights(new ReelInsightModel(0, 0));
        reelModel.setStatus(ReviewStatus.REVIEW_COMPLETE);
        reelModel.setUploadedBy("admin");
        if(reelModel.getVideo()!=null){
           if(reelModel.getVideo().getExternalFile()!=null){
               if(reelModel.getVideo().getExternalFile().getUrl()!=null){
                   String platform = thumbnailExtractorService.platformFromUrl(reelModel.getVideo().getExternalFile().getUrl());
                   System.out.println("platform"+platform);
                   if (platform != null && platform.equalsIgnoreCase("youtube")) {
                       reelModel.getVideo().getExternalFile().setThumbnailUrl(getYouTubeThumbnail(reelModel.getVideo().getExternalFile().getUrl()));
                   } else if (platform != null && platform.equalsIgnoreCase("facebook")) {
                       reelModel.getVideo().getExternalFile().setThumbnailUrl(thumbnailExtractorService.extractFacebookThumbnail(reelModel.getVideo().getExternalFile().getUrl()));
                   } else if (platform != null && platform.equalsIgnoreCase("twitter")) {
                       String thumbnail = thumbnailExtractorService.extractTwitterThumbnail(reelModel.getVideo().getExternalFile().getUrl());
                       if (thumbnail != null) {
                           reelModel.getVideo().getExternalFile().setThumbnailUrl(thumbnail);
                       } else {
                           reelModel.getVideo().getExternalFile().setThumbnailUrl("https://rtvnews-media.s3.ap-south-1.amazonaws.com/images/twitter_thumbnail.png");
                       }
                   }
               }
           }
        }

        reelModel.setIsApproved(true);
        //TODO: "later i have to add notification "
        return reelRepository.save(reelModel);
    }

    public static String getYouTubeThumbnail(String input) {
        if(input==null){
            return null;
        }
        // Check if input is a valid URL
        URL url;
        try {
            url = new URL(input);
        } catch (MalformedURLException e) {
            return null; // Not a valid URL
        }

        // Check if host contains "youtube.com" or "youtu.be"
        String host = url.getHost();
        if (!(host.contains("youtube.com") || host.contains("youtu.be"))) {
            return null; // Not a YouTube URL
        }

        // Patterns to extract video ID
        String[] patterns = {
                "youtube\\.com/shorts/([^?]+)",
                "youtube\\.com/watch\\?v=([^&]+)",
                "youtu\\.be/([^?]+)"
        };

        // Try to match and return thumbnail URL
        for (String regex : patterns) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
                return "https://img.youtube.com/vi/" + matcher.group(1) + "/sddefault.jpg";
            }
        }
        return null; // Video ID not found
    }
    @Override
    public ReelModel approveReel(String reelId, Boolean isApproved, RejectionReasonDto rejectionReason) {
        ReelModel reel = reelRepository.findById(reelId)
                .orElseThrow(() -> new RuntimeException("Reel not found"));
        reel.setStatus(ReviewStatus.REVIEW_COMPLETE);
        reel.setIsApproved(isApproved);
        if(!isApproved){
        reel.setRejectionReason(rejectionReason.getRejectionReason());
        }
        return reelRepository.save(reel);
    }

    @Override
    public List<ReelModel> getAllReels(String userId, Pageable pageable) {
        Page<ReelModel> reels = reelRepository.findByStatusAndIsApprovedNotFalse(
                ReviewStatus.REVIEW_COMPLETE,
                pageable
        );

        // Mark liked for each reel
        return reels.map(reel -> {
            boolean isLiked = likeRepository.existsByUserIdAndContentId(userId, reel.getId());
            reel.setLiked(isLiked);
            return reel;
        }).toList();
    }

    @Override
    public List<ReelModel> getAllReelsByUserId(String userId, ReviewStatus status, Pageable pageable) {
        return reelRepository.findByUserIdAndStatus(userId, status, pageable).toList();
    }

    @Override
    public ReelModel ReUploadReelByUserId(String userId, ReelModel reelModel) {
        ReelModel existing = reelRepository.findById(reelModel.getId())
                .orElseThrow(() -> new RuntimeException("Reel not found"));

        existing.setTitle(reelModel.getTitle());
        if(reelModel.getVideo()!=null){
            existing.setVideo(reelModel.getVideo());
        }
        existing.setStatus(ReviewStatus.UNDER_REVIEW);
        existing.setRejectionReason(null);
        existing.setIsApproved(false);
        existing.setUploadedBy("user");
        return reelRepository.save(existing);
    }

    @Override
    public List<ReelModel> getAllReelsByUserId(String userId, Pageable pageable) {
        return reelRepository.findByUserId(userId,pageable);
    }


    private CommentModel resolveTaggedUsers(CommentModel comment) {
        if (comment.getTaggedUsers() == null || comment.getTaggedUsers().isEmpty()) {
            return comment;
        }

        List<TaggedUser> resolved = comment.getTaggedUsers()
                .stream()
                .map(taggedUser -> userRepository.findById(taggedUser.getUserId())
                        .map(user -> new TaggedUser(user.getId(),
                                user.getName() != null ? user.getName() : "Unknown"))
                        .orElse(new TaggedUser(taggedUser.getUserId(), "Unknown"))
                )
                .collect(Collectors.toList());

        List<String> userList = resolved.stream()
                .map(TaggedUser::getUserId)
                .collect(Collectors.toList());

        comment.setTaggedUsers(resolved);
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("title", "You’ve been tagged 🔗 in a reels.");
        eventData.put("path", "/reel/" + comment.getContentId());
        eventData.put("userlist", userList);
//        produceEventToKafka(EventType.OTHERS, null, KafkaTopicConfig.NOTIFICATIONS_CHANNEL, eventData);

        return comment;
    }

    @Override
    public EventService getEventService() {
        return eventService;
    }

    @Override
    public List<ReelResponseDTO> getAllReelsSearch(ReviewStatus status, String query, Pageable pageable) {

        List<ReelModel> reels;

        boolean hasQuery = query != null && !query.isBlank();
        boolean hasStatus = status != null;

        // If searching with both query + status
        if (hasQuery && hasStatus) {
            reels = reelRepository.searchByTitleAndStatus(query, status, pageable).toList();
        }
        // Only query
        else if (hasQuery) {
            reels = reelRepository.searchByTitle(query, pageable).toList();
        }
        // Only status
        else if (hasStatus) {
            reels = reelRepository.findByStatus(status, pageable).toList();
        }
        // No filter
        else {
            reels = reelRepository.findAll(pageable).toList();
        }

        return reels.stream()
                .map(reel -> {

                    String username = "Unknown";
                    String phoneNumber = null;
                    RelativePath profileImage = null;

                    if (reel.getUserId() != null && !reel.getUserId().isBlank()) {

                        User user = userRepository.findById(reel.getUserId()).orElse(null);

                        if (user != null) {
                            username = user.getName();
                            phoneNumber = user.getPhoneNumber();
                            profileImage = user.getProfileImage();
                        }
                    }

                    return new ReelResponseDTO(
                            reel.getId(),
                            reel.getUserId(),
                            username,
                            reel.getTitle(),
                            reel.getVideo(),
                            reel.getInsights(),
                            reel.getIsApproved(),
                            reel.getUploadedBy(),
                            reel.getStatus(),
                            reel.getRejectionReason(),
                            reel.getCreatedAt(),
                            reel.getUpdatedAt(),
                            reel.isLiked(),
                            phoneNumber,
                            profileImage
                    );
                })
                .toList();
    }

}
