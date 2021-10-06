package com.example.backend.dao;

import com.example.backend.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionDao extends JpaRepository<Question, Integer> {
    Question findById(int id);
}
