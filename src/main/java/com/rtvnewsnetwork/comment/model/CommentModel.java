package com.rtvnewsnetwork.comment.model;

import com.rtvnewsnetwork.common.model.CONTENT_TYPE;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentModel {

    @Id
    private String id;

    private String userId;
    private String username;
    private String comment;
    @ArraySchema(
            schema = @Schema(ref = "#/components/schemas/CommentModel", description = "List of replies to this comment")
    )
    private List<CommentModel> replies = new ArrayList<>();
    private List<TaggedUser> taggedUsers = new ArrayList<>();
    private String contentId; // can be postId or reelId
    private CONTENT_TYPE targetType; // "POST" or "REEL"

    @CreatedDate
    private Instant createdAt;
}

