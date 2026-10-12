package com.rtvnewsnetwork.user.model;

import com.rtvnewsnetwork.config.model.UploadedFile.RelativePath;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchDto {
    private String name;
    private String id;
    @Schema(implementation = String.class, description = "Profile image path")
    private RelativePath profileImage;
}
