package com.rtvnewsnetwork.district.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class DistrictModel {
    @Id
    private String id;
    @NotBlank(message = "District name is required")
    private String name;
}
