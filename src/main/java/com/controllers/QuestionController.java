package com.controllers;

import com.model.Question;
import com.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.controllers.SurveyController.checkSortPage;

@RestController
@RequiredArgsConstructor
@RequestMapping("/questions")
@Tag(name = "Question Controller")
public class QuestionController {
    private final QuestionService questionService;

    @Operation(summary = "Adding new question")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Added a question", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Question.class)) }),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)})
    @PostMapping("/add")
    public ResponseEntity<?> addQuestion(
        @RequestParam(name = "questionBody") String questionBody) {
        return new ResponseEntity<>(questionService.addQuestion(questionBody), HttpStatus.OK);
    }

    @Operation(summary = "Getting questions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Got a question", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Question.class)) }),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)})
    @GetMapping("/get")
    public ResponseEntity<?> getQuestions(
        @RequestParam(name = "order", required = false, defaultValue = "ASC") @Parameter(description = "ASC || DESC") String order,
        @RequestParam(name = "by", required = false, defaultValue = "id") @Parameter(description = "id || question") String sortBy,
        @RequestParam(name = "page", required = false, defaultValue = "1") @Parameter(description = "Page number") Integer page,
        @RequestParam(name = "quantity", required = false, defaultValue = "10") @Parameter(description = "Number of results on page") Integer pageSize
    ) {
        if (order == null) order = "asc";
        if (page == null) page = 1;
        if (pageSize == null) page = 10;
        if (!(sortBy.equalsIgnoreCase("id") || sortBy.equalsIgnoreCase("question"))) {
            return new ResponseEntity<>("Wrong parameter sortBy. Either by 'id' or 'question'", HttpStatus.BAD_REQUEST);
        }
        PageRequest request = checkSortPage(order, sortBy, page, pageSize);

        return new ResponseEntity<>(questionService.getQuestions(request),HttpStatus.OK);
    }

    @Operation(summary = "Getting question by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Got question by id", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Question.class)) }),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)})
    @GetMapping("/getOne")
    public ResponseEntity<?> getQuestionById(
        @RequestParam(name = "id") @Parameter(description = "Id of a question") Long id
    ) {
        if (id < 0)
            return new ResponseEntity<>("ID must be > 0", HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(questionService.getQuestionById(id), HttpStatus.OK);
    }

    @Operation(summary = "Updating question")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Updated a question", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Question.class)) }),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)})
    @PutMapping("/update")
    public ResponseEntity<?> updateQuestion(
        @RequestParam(name = "id") Long id,
        @RequestParam(name = "question") String question
    ) {
        if (id < 0)
            return new ResponseEntity<>("ID must be > 0", HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(questionService.updateQuestion(question, id), HttpStatus.OK);
    }

    @Operation(summary = "Deleting question")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Deleted a question", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Question.class)) }),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)})
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteQuestion(
        @RequestParam(name = "id") Long id
    ) {
        if (id < 0)
            return new ResponseEntity<>("ID must be > 0", HttpStatus.BAD_REQUEST);

        return questionService.deleteById(id);
    }

}
