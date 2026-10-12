package com.rtvnewsnetwork.quiz.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmissionRequest {
    @NotBlank(message = "Quiz ID cannot be empty")
    private String quizId;
    @NotBlank(message = "Question ID cannot be empty")
    private String questionId;
    @NotBlank(message = "Option ID cannot be empty")
    private String optionId;
}
