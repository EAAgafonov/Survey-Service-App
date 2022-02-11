package com.dao;

import com.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository
        extends JpaRepository<Question, Long> {
}
