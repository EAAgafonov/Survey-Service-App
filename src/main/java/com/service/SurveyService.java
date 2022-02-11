package com.service;

import com.model.Question;
import com.model.Survey;
import com.dao.QuestionRepository;
import com.dao.SurveyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyService {
    private final SurveyRepository surveyRepository;
    private final QuestionRepository questionRepository;

    public Survey createSurvey(String surveyName, String start_date, String end_date, boolean isActive, List<Long>questionId) {
        log.info("Creating survey");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Survey survey  = new Survey();
        List<Question> questionsToAdd = new ArrayList<>();

        survey.setSurveyName(surveyName);
        survey.setStartDate(LocalDate.parse(start_date, formatter));
        survey.setEndDate(LocalDate.parse(end_date, formatter));
        survey.setActive(isActive);
        questionId.forEach((id) -> {
            if (questionRepository.findById(id).isPresent())
                questionsToAdd.add(questionRepository.findById(id).get());
        });

        survey.setQuestions(questionsToAdd);
        return survey;
    }

    public ResponseEntity<?> saveSurvey(Survey survey) {
        log.info("Saving survey to db");
        return new ResponseEntity<>(surveyRepository.save(survey), HttpStatus.OK);
    }

    public ResponseEntity<?> updateSurvey(Long id, Survey infoToChange) {
        log.info("Updating survey");
        if (surveyRepository.findById(id).isPresent()) {
            Survey survey = surveyRepository.getById(id);
            survey.setSurveyName(infoToChange.getSurveyName());
            survey.setStartDate(infoToChange.getStartDate());
            survey.setEndDate(infoToChange.getEndDate());
            survey.setActive(infoToChange.isActive());
            survey.setQuestions(infoToChange.getQuestions());
            return new ResponseEntity<>(surveyRepository.save(survey), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("There's no such survey", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> getSurveyById(Long id) {
        log.info("Getting survey by id");
        if (surveyRepository.findById(id).isPresent())
            return new ResponseEntity<>(surveyRepository.findById(id), HttpStatus.OK);
        else
            return new ResponseEntity<>("There's no such survey", HttpStatus.BAD_REQUEST);
    }

    public Survey getSurveyItself(Long id) {
        if (surveyRepository.findById(id).isPresent())
            return surveyRepository.findById(id).get();

        return null;
    }

    public ResponseEntity<?> deleteSurvey(long id) {
        log.info("Deleting survey");
        if (surveyRepository.findById(id).isPresent()) {
            surveyRepository.deleteById(id);
            return new ResponseEntity<>("Deleted survey with id - " + id, HttpStatus.OK);
        }
        else
            return new ResponseEntity<>("There's no such survey", HttpStatus.BAD_REQUEST);
    }

    public Page<Survey> getSurveys(PageRequest request) {
        log.info("Getting surveys with filters");
        return surveyRepository.findAll(request);
    }

    public ResponseEntity<?> getQuestionsFromSurvey(Long id) {
        log.info("Getting questions from one survey");
        if (surveyRepository.findById(id).isPresent()) {
            List<Question> questions = surveyRepository.findById(id).get().getQuestions();
            return new ResponseEntity<>(questions, HttpStatus.OK);
        }
        else
            return new ResponseEntity<>("There's no such survey", HttpStatus.BAD_REQUEST);
    }



}

