package com.service;

import com.model.Question;
import com.dao.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionService {
    private final QuestionRepository questionRepository;

    public Question addQuestion(String questionBody) {
        log.info("Adding a question");
        Question question = new Question();
        question.setQuestion(questionBody);
        return questionRepository.save(question);
    }

    public Page<Question> getQuestions(PageRequest request) {
        log.info("Getting questions with filters");
        return questionRepository.findAll(request);
    }

    public ResponseEntity<?> getQuestionById(Long id) {
        log.info("Getting one question by id");
        Optional<Question> questionOptional = questionRepository.findById(id);
        if (questionOptional.isPresent()) {
            return new ResponseEntity<>(questionOptional.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("There's no such question", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> updateQuestion(String newQuestionBody, Long id) {
        log.info("Updating question");
        Optional<Question> questionOptional = questionRepository.findById(id);
        if (questionOptional.isPresent()) {
            Question question = questionOptional.get();
            question.setQuestion(newQuestionBody);
            return new ResponseEntity<>(questionRepository.save(question), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("There's no such question", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> deleteById(Long id) {
        log.info("Deleting question");
        Optional<Question> questionOptional = questionRepository.findById(id);
        if (questionOptional.isPresent()) {
            questionRepository.deleteById(id);
            return new ResponseEntity<>("Deleted question with id - " + id, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("There's no such question", HttpStatus.BAD_REQUEST);
        }
    }



}
