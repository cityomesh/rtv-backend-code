package com.rtvnewsnetwork.news.model;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryResponse {
    @NotBlank(message = "Category id is required")
    private int id;
    @NotBlank(message = "Category label is required")
    private String label;
    private String iconUrl;
}
