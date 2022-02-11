package com.service;

import com.dao.QuestionRepository;
import com.dao.SurveyRepository;
import com.model.Question;
import com.model.Survey;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SurveyServiceTest {
    @Mock
    private SurveyRepository surveyRepository;
    @Mock
    private QuestionRepository questionRepository;
    private SurveyService surveyService;

    private static Question question = mock(Question.class);
    private static Survey survey = mock(Survey.class);
    private static List<Question> list = new ArrayList<>();

    @BeforeAll
    static void beforeAll() {
        question = new Question();
        question.setQuestion("Тестовый вопрос");
        question.setId(1L);

        survey = new Survey();
        survey.setSurveyName("test");
        survey.setActive(true);
        survey.setStartDate(LocalDate.of(2022, 5, 5));
        survey.setEndDate(LocalDate.of(2022, 5, 10));
        survey.setId(1L);
        list.add(question);
        survey.setQuestions(list);
    }

    @BeforeEach
    void setUp() {
        surveyService = new SurveyService(surveyRepository, questionRepository);
    }

    @Test
    void createSurvey() {
        ArrayList<Long> test = new ArrayList<>(Collections.singletonList(1L));
        surveyService.createSurvey("test", "2022-05-05", "2022-05-10", false, test);
        verify(questionRepository).findById(question.getId());
    }

    @Test
    void deleteSurvey() {
        surveyService.deleteSurvey(1L);
        if (surveyRepository.findById(question.getId()).isPresent())
            verify(surveyRepository).deleteById(question.getId());
    }

    @Test
    void getSurveys() {
    }

    @Test
    void getQuestionsFromSurvey() {
        surveyService.getQuestionsFromSurvey(1L);
        if (surveyRepository.findById(question.getId()).isPresent())
            verify(surveyRepository).findById(question.getId()).get().getQuestions();

    }
}