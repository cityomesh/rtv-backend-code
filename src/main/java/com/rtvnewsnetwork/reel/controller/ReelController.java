package com.rtvnewsnetwork.reel.controller;

import com.rtvnewsnetwork.comment.model.CommentModel;
import com.rtvnewsnetwork.common.service.AuthDetailsHelper;
import com.rtvnewsnetwork.post.model.PostModel;
import com.rtvnewsnetwork.reel.dto.ReelResponseDTO;
import com.rtvnewsnetwork.reel.model.ReelInsightModel;
import com.rtvnewsnetwork.reel.model.ReelModel;
import com.rtvnewsnetwork.reel.model.RejectionReasonDto;
import com.rtvnewsnetwork.reel.model.ReviewStatus;
import com.rtvnewsnetwork.reel.service.ReelService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/reels")
public class ReelController implements AuthDetailsHelper {

    private final ReelService reelService;

    public ReelController(ReelService reelService) {
        this.reelService = reelService;
    }


    @PostMapping
    public ResponseEntity<ReelModel> addReel(@RequestBody ReelModel reelModel) {
        String userId = getUserId();
        if(userId==null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User ID is null");
        }
        return new ResponseEntity<>(reelService.addReel(userId, reelModel), HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ReelModel> updateReel(
            @PathVariable String id,
            @RequestBody ReelModel reelModel
    )throws Exception {
        return ResponseEntity.ok(reelService.updateReel(id, reelModel));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ReelModel> deleteReel(@PathVariable String id) {
        return ResponseEntity.ok(reelService.deleteReel(id));
    }


//    @GetMapping("/dashboard")
//    public ResponseEntity<List<ReelModel>> getAllReels(
//            @RequestParam(value = "status", required = false) ReviewStatus status
//            ,@RequestParam(value = "page", defaultValue = "0") int page,
//            @RequestParam(value = "size", defaultValue = "10") int size
//    ) {
//        return ResponseEntity.ok(
//                reelService.getAllReels(
//                        status,
//                        PageRequest.of(page, size, Sort.by("createdAt").descending())
//                )
//        );
//    }

    @GetMapping("/dashboard")
    public ResponseEntity<List<ReelResponseDTO>> getAllReels(
            @RequestParam(value = "status", required = false) ReviewStatus status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        List<ReelResponseDTO> reels = reelService.getAllReels(status, pageable);

        return ResponseEntity.ok(reels);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ReelModel> getReelById(@PathVariable String id) {
        return ResponseEntity.ok(reelService.getReelById(id));
    }


//    @PostMapping("/{id}/likes")
//    public ResponseEntity<ReelModel> incrementLikes(
//            @PathVariable String id,
//            @RequestParam String userId
//    ) {
//        return ResponseEntity.ok(reelService.incrementLikes(id, userId));
//    }
//
//
//    @DeleteMapping("/{id}/likes")
//    public ResponseEntity<ReelModel> decrementLikes(
//            @PathVariable String id,
//            @RequestParam String userId
//    ) {
//        return ResponseEntity.ok(reelService.decrementLikes(id, userId));
//    }

    @PostMapping("{reelId}/like")
    public ReelModel addLikeOrRemove(
            @PathVariable(required = true) String reelId,
            @RequestParam(required = true) boolean add
    ) {
        String userId = getUserId();
        if (add) {
            return reelService.incrementLikes(reelId, userId);
        } else {
            return reelService.decrementLikes(reelId, userId);
        }
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentModel> addComment(
            @PathVariable String id,
            @RequestBody CommentModel commentModel
    ) {
        String userId = getUserId();
        String userName = getUser() != null ? getUser().getName() : null;
        return new ResponseEntity<>(reelService.addComment(id, userId,userName,commentModel), HttpStatus.CREATED);
    }


    @PostMapping("/{id}/comments/{parentId}/replies")
    public ResponseEntity<CommentModel> addReply(
            @PathVariable String id,
            @PathVariable String parentId,
            @RequestBody CommentModel reply
    ) {
        return new ResponseEntity<>(reelService.addReply(id, parentId, reply), HttpStatus.CREATED);
    }


    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentModel>> getCommentsByReelId(
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(reelService.getCommentsByReelId(id, pageable));
    }


    @DeleteMapping("/{id}/comments/{commentId}")
    public ResponseEntity<ReelInsightModel> deleteComment(
            @PathVariable String id,
            @PathVariable String commentId
    ) {
        return ResponseEntity.ok(reelService.deleteComment(id, commentId));
    }


    @PutMapping("/{id}/comments/{commentId}")
    public ResponseEntity<CommentModel> editComment(
            @PathVariable String id,
            @PathVariable String commentId,
            @RequestParam String updatedComment
    ) {
        return ResponseEntity.ok(reelService.editComment(id, commentId, updatedComment));
    }

    @PostMapping("/admin")
    public ReelModel addReelByAdmin(@RequestBody ReelModel reelModel) throws   Exception {
        return reelService.addReelByAdmin(reelModel);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{id}/approve")
    public ReelModel approveReel(@PathVariable String id,@RequestParam Boolean isApproved,@RequestBody(required = false) RejectionReasonDto rejectionReason ) {
        return reelService.approveReel(id,isApproved,rejectionReason);
    }


    @GetMapping("/user")
    public List<ReelModel> getAllReels(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ){
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt").descending());
        String userId = getUserId();
        return reelService.getAllReels(userId, pageable);
    }

    @GetMapping("/user/reels")
    public List<ReelModel> getReelsByUserId(
            @RequestParam(defaultValue = "REVIEW_COMPLETE") ReviewStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String userId = getUserId();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return  reelService.getAllReelsByUserId(userId, status, pageable);

    }


    @PutMapping("/user/{id}")
    public ReelModel ReUploadReelByUserId(@PathVariable String id, @RequestBody ReelModel reelModel) {
        String userId = getUserId();
        if(userId==null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User ID is null");
        }
        return reelService.ReUploadReelByUserId(userId, reelModel);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/user/all")
    public List<ReelModel> getReelsByUserId(  @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        String userId = getUserId();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return reelService.getAllReelsByUserId(userId, pageable);
    }

    @GetMapping("/dashboard/search")
    public ResponseEntity<List<ReelResponseDTO>> getAllReels(
            @RequestParam(value = "status", required = false) ReviewStatus status,
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        List<ReelResponseDTO> reels = reelService.getAllReelsSearch(status, query, pageable);

        return ResponseEntity.ok(reels);
    }

}
