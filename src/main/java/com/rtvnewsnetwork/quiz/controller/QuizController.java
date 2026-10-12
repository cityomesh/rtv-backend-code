package com.rtvnewsnetwork.quiz.controller;

import com.rtvnewsnetwork.common.service.AuthDetailsHelper;
import com.rtvnewsnetwork.quiz.model.FilterEnum;
import com.rtvnewsnetwork.quiz.model.QuizFeed;
import com.rtvnewsnetwork.quiz.model.QuizModel;
import com.rtvnewsnetwork.quiz.model.QuizSubmissionRequest;
import com.rtvnewsnetwork.quiz.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Quiz APIs", description = "Endpoints for managing and retrieving quizzes")
public class QuizController implements AuthDetailsHelper {

    private final QuizService quizService;

    @Autowired
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/api/quiz/feed")
    public List<QuizFeed> findQuizFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Extract user ID from the security context
        String userId = getUserId();

        // Construct the quiz feed
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return quizService.getQuizFeed(userId, pageable);
    }

    @PostMapping("/api/quiz/feed/answer")
    public QuizFeed submitQuizAnswer(@RequestBody QuizSubmissionRequest request) {
        String userId = getUserId();
        return quizService.submitQuiz(userId, request);
    }

    @GetMapping("/api/quiz/feed/{id}")
    public QuizFeed getQuizById(@PathVariable("id") String id) {
        String userId = getUserId();
        return quizService.getQuizById(userId, id);
    }

    @GetMapping("/api/quiz/topfans")
    public Map<String, Object> getTopUsers(
            @RequestParam(name = "limit", defaultValue = "100") int limit,
            @RequestParam(name = "filterType", defaultValue = "week") String filterType) {

        String userId = getUserId();

        try {
            FilterEnum.valueOf(filterType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid filter type. Use 'WEEK' or 'MONTH'.");
        }

        return quizService.getTopUsers(limit, userId, filterType);
    }

    @PostMapping("/api/quiz")
    @Operation(
            summary = "Create a new quiz",
            description = "Creates a new quiz ",
            tags = { "Quiz Management" })
    public QuizModel createQuiz(@RequestBody QuizModel quizModel) {
        try {
            return quizService.createQuiz(quizModel);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input data");
        }
    }
    @DeleteMapping("/api/quiz/{id}")
    @Operation(
            summary = "Delete a quiz by ID",
            description = "Deletes an existing quiz from the database using its unique ID. ",
            tags = { "Quiz Management" }
    )
    public QuizModel deleteQuiz(@PathVariable("id") String id) {
        return quizService.deleteQuiz(id);
    }

    @PutMapping("/api/quiz/{id}")
    @Operation(
            summary = "Update an existing quiz",
            description = "Updates an existing quiz by its ID. ",
            tags = { "Quiz Management" }
    )
    public QuizModel updateQuiz(@PathVariable("id") String id, @RequestBody QuizModel quizModel) {
        return quizService.updateQuiz(id, quizModel);
    }

    @GetMapping("/api/quiz/{id}")
    @Operation(
            summary = "Fetch a quiz by ID for dashboard",
            description = "This API retrieves a single quiz using its ID. It will be used to show quiz details on the dashboard.",
            tags = { "Dashboard" }
    )
    public QuizModel getQuizByIdForDashboard(@PathVariable("id") String id) {
        return quizService.getQuizForDashboard(id);
    }

    @GetMapping("/api/quiz")
    @Operation(
            summary = "Fetch all quizzes for dashboard",
            description = "This API retrieves a paginated list of quizzes sorted by creation date. It will be used to show quizzes on the dashboard.",
            tags = { "Dashboard" }
    )
    public List<QuizModel> getAllQuiz(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        // Extract user ID from the security context
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return quizService.getAllQuizForDashboard(pageable);
    }


    @GetMapping("/api/quiz/search")
    @Operation(
            summary = "Fetch all quizzes for dashboard",
            description = "This API retrieves a paginated list of quizzes sorted by creation date, with optional keyword search.",
            tags = { "Dashboard" }
    )
    public List<QuizModel> getAllQuiz(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestParam(required = false) String query) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return quizService.getAllQuizForDashboardSeach(pageable, query);
    }

}

