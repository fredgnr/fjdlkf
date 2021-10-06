package com.example.backend.dao;

import com.example.backend.entity.ExamQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamQuestionDao extends JpaRepository<ExamQuestion, Integer> {
    ExamQuestion findExamQuestionById(int id);
}
