package com.rtvnewsnetwork.user.model;

import com.rtvnewsnetwork.config.model.UploadedFile.RelativePath;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @NotBlank(message = "Name is required")
    private String name;
    private String email;
    private Gender gender;
    private Integer age;
    @Schema(implementation = String.class, description = "Profile image path")
    private RelativePath profileImage;
}
