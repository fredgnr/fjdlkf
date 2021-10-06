package com.example.backend.service.Impl;

import com.example.backend.dao.QuestionDao;
import com.example.backend.entity.Question;
import com.example.backend.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuestionServiceImpl implements QuestionService {
    @Autowired
    QuestionDao questionDao;

    @Override
    public Question getById(int id) {
        return questionDao.getById(id);
    }

    @Override
    @Transactional
    public void save(Question question) {
        questionDao.save(question);
    }
}
