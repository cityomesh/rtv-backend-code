package com.rtvnewsnetwork.quiz.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class QuizModel {
    @Id
    private String id;
    @NotBlank(message = "Questions cannot be empty")
    private List<QuizQuestion> questions;
    @NotBlank(message = "Quiz status cannot be empty")
    private QuizStatus status;
    @NotBlank(message = "Reward coins per question cannot be empty")
    private int rewardCoinsPerQuestion;
//    private ShareModel share;
//    private String shareUrl;
//    @NotBlank(message = "Quiz type cannot be empty")
//    private QuizTypeEnum quizType;
    private Long responseCount=0L;
    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}