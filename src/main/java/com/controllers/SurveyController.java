package com.controllers;

import com.model.Survey;
import com.service.SurveyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("/surveys")
@Tag(name = "Survey Controller")
public class SurveyController {
    private final SurveyService surveyService;

    @Operation(summary = "Adding a survey")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Added a survey", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Survey.class)) }),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)})
    @PostMapping("/add")
    public ResponseEntity<?> addSurvey(
        @RequestParam(name = "surveyName") @Parameter(description = "Survey name here") String surveyName,
        @RequestParam(name = "surveyStart") @Parameter(description = "Start Date yyyy-MM-dd") String start_date,
        @RequestParam(name = "surveyEnd") @Parameter(description = "End Date yyyy-MM-dd") String end_date,
        @RequestParam(name = "surveyIsActive", defaultValue = "false") @Parameter(description = "isActive") Boolean isActive,
        @RequestParam(name = "surveyQuestions") @Parameter(description = "New questions id's Ex. 1,2,3") List<Long> questionsId
    ) {
        if (checkDate(start_date, end_date))
            return new ResponseEntity<>("Invalid date format! Try yyyy-MM-dd || startDate must be before endDate", HttpStatus.BAD_REQUEST);

        Survey survey = surveyService.createSurvey(surveyName, start_date, end_date, isActive, questionsId);
        return new ResponseEntity<>(surveyService.saveSurvey(survey), HttpStatus.OK);
    }

    @Operation(summary = "Updating a survey")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Updated a survey", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Survey.class)) }),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)})
    @PutMapping("/update")
    public ResponseEntity<?> updateSurvey(
        @RequestParam("id") @Parameter(description = "Id of a survey to update") Long id,
        @RequestParam(name = "surveyName", required = false) @Parameter(description = "New survey name here") String surveyName,
        @RequestParam(name = "surveyStart", required = false) @Parameter(description = "New startDate yyyy-MM-dd") String start_date,
        @RequestParam(name = "surveyEnd", required = false) @Parameter(description = "New endDate yyyy-MM-dd") String end_date,
        @RequestParam(name = "surveyIsActive", required = false, defaultValue = "false") @Parameter(description = "New isActive") Boolean isActive,
        @RequestParam(name = "questionsId", required = false)@Parameter(description = "New questions") ArrayList<Long> questionsId
    ) {
        if (id < 0) return new ResponseEntity<>("ID must be > 0", HttpStatus.BAD_REQUEST);
        if (surveyService.getSurveyItself(id) == null) return new ResponseEntity<>("There's no survey with that id", HttpStatus.BAD_REQUEST);

        if (start_date == null) start_date = surveyService.getSurveyItself(id).getStartDate().toString();
        if (end_date == null) end_date = surveyService.getSurveyItself(id).getEndDate().toString();

        if (checkDate(start_date, end_date)) {
            return new ResponseEntity<>("Invalid date format! Try yyyy-MM-dd || startDate must be before endDate", HttpStatus.BAD_REQUEST);
        }

        if (questionsId == null)  questionsId = new ArrayList<>();
        if (surveyName == null) {
            Survey oldSurvey = surveyService.getSurveyItself(id);
            if (!(oldSurvey == null))
                surveyName = oldSurvey.getSurveyName();
            else surveyName = "";
        }
        Survey survey = surveyService.createSurvey(surveyName, start_date, end_date, isActive, questionsId);
        return new ResponseEntity<>(surveyService.updateSurvey(id, survey), HttpStatus.OK);
    }

    @Operation(summary = "Get survey by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Got survey by id", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Survey.class)) }),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)})
    @GetMapping("/getOne")
    public ResponseEntity<?> getSurveyById(
        @RequestParam("id") @Parameter(description = "Id of a survey to get") Long id
    ) {
        if (id < 0)
            return new ResponseEntity<>("ID must be > 0", HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(surveyService.getSurveyById(id), HttpStatus.OK);
    }

    @Operation(summary = "Delete survey by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Deleted a survey", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Survey.class)) }),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)})
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteSurvey(
        @RequestParam("id") @Parameter(description = "Id of a survey to delete") Long id
    ) {
        if (id < 0)
            return new ResponseEntity<>("ID must be > 0", HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(surveyService.deleteSurvey(id), HttpStatus.OK);
    }

    @Operation(summary = "Getting surveys")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Got surveys", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Survey.class)) }),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)})
    @GetMapping("/get")
    public ResponseEntity<?> getSurveys(
        @RequestParam(name = "order", defaultValue = "ASC") @Parameter(description = "Sort order - (ASC||DESC)") String order,
        @RequestParam(name = "by", defaultValue = "surveyName") @Parameter(description = "(surveyName||startDate)") String sortBy,
        @RequestParam(name = "page", defaultValue = "1") @Parameter(description = "Page number") Integer page,
        @RequestParam(name = "quantity", defaultValue = "10") @Parameter(description = "Number of results on page") Integer pageSize
    ) {
        if (!(sortBy.equalsIgnoreCase("surveyName") || sortBy.equalsIgnoreCase("startDate"))) {
            return new ResponseEntity<>("Wrong parameter sortBy. Either by 'name' or 'date'", HttpStatus.BAD_REQUEST);
        }
        if (order == null) order = "asc";
        if (page == null) page = 1;
        if (pageSize == null) page = 10;


        PageRequest request = checkSortPage(order, sortBy, page, pageSize);
        return new ResponseEntity<>(surveyService.getSurveys(request),HttpStatus.OK);
    }

    @Operation(summary = "Get questions from a survey")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Questions from surveys", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Survey.class)) }),
        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)})
    @GetMapping("/get/questions")
    public ResponseEntity<?> getQuestionsFromSurvey(
        @RequestParam("id") @Parameter(description = "Id of a survey to get question from") Long id
    ) {
        if (id < 0)
            return new ResponseEntity<>("ID must be > 0", HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(surveyService.getQuestionsFromSurvey(id), HttpStatus.OK);
    }




    static PageRequest checkSortPage(String order, String sortBy, Integer page, Integer pageSize) {
        page = (page < 0) ? 0 : (page - 1);
        pageSize = (pageSize < 0) ? 10 : pageSize;

        Sort sort;
        order = order.toLowerCase(Locale.ROOT);
        if (order.equals("desc")) {
            sort = Sort.by(sortBy).descending();
        } else {
            sort = Sort.by(sortBy).ascending();
        }

        return PageRequest.of(page, pageSize, sort);
    }

    private boolean checkDate(String start_date, String end_date) {
        try {
            LocalDate start = LocalDate.parse(start_date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate end = LocalDate.parse(end_date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return start.isAfter(end);
        } catch (DateTimeParseException e) {
            return true;
        }
    }

}
