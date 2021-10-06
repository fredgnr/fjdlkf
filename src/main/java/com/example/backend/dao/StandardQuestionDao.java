package com.example.backend.dao;

import com.example.backend.entity.StandardQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StandardQuestionDao extends JpaRepository<StandardQuestion, Integer> {
    StandardQuestion findStandardById(int id);
}

