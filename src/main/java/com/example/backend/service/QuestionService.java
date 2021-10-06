package com.example.backend.service;

import com.example.backend.entity.Question;

public interface QuestionService {
    Question getById(int id);

    void save(Question question);
}
