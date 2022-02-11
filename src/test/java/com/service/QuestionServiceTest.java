package com.service;

import com.dao.QuestionRepository;
import com.model.Question;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {
    @Mock
    private QuestionRepository questionRepository;
    private QuestionService questionService;
    private static Question question = mock(Question.class);

    @BeforeAll
    static void beforeAll() {
        question = new Question();
        question.setQuestion("Тестовый вопрос");
        question.setId(null);
    }

    @BeforeEach
    void setUp() {
        questionService = new QuestionService(questionRepository);
    }

    @Test
    void addQuestion() {
        questionService.addQuestion("Тестовый вопрос");
        verify(questionRepository).save(question);
    }

    @Test
    void getQuestions() {
        PageRequest request = PageRequest.of(1, 10, Sort.by("id").ascending());
        questionService.getQuestions(request);
        verify(questionRepository).findAll(request);
    }

    @Test
    void getQuestionById() {
        questionService.getQuestionById(1L);
        question.setId(1L);
        verify(questionRepository).findById(question.getId());

    }

    @Test
    void updateQuestion() {
        questionService.updateQuestion("New Body", 1L);
        question.setId(1L);
        verify(questionRepository).findById(question.getId());
    }

    @Test
    void deleteById() {
        questionService.deleteById(1L);
        question.setId(1L);
        verify(questionRepository).findById(question.getId());
        if (questionRepository.findById(question.getId()).isPresent()) {
            verify(questionRepository).deleteById(question.getId());
        }
    }
}