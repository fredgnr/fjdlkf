package com.example.backend.dao;

import com.example.backend.entity.ApplyQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplyQuestionDao extends JpaRepository<ApplyQuestion, Integer> {
    ApplyQuestion findApplyQuestionById(int id);
}
