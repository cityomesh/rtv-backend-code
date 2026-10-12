package com.rtvnewsnetwork.post.model;

import com.rtvnewsnetwork.config.model.UploadedFile.RelativePath;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InternalFile {
    @Schema(implementation = String.class, description = "post thumbnail image path")
    private RelativePath thumbnail;
    @Schema(implementation = String.class, description = "post video  path")
    private RelativePath video;
}
