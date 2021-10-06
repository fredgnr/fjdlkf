package com.example.backend.service.Impl;

import com.example.backend.dao.ExamQuestionDao;
import com.example.backend.dao.ExamQuestionnaireDao;
import com.example.backend.dao.WrittenDao;
import com.example.backend.entity.ExamQuestion;
import com.example.backend.entity.ExamQuestionnaire;
import com.example.backend.entity.IsWritten;
import com.example.backend.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class ExamServiceImpl implements ExamService {
    @Autowired
    ExamQuestionDao examQuestionDao;
    @Autowired
    ExamQuestionnaireDao examQuestionnaireDao;
    @Autowired
    WrittenDao writtenDao;

    @Override
    @Transactional
    public void save(ExamQuestionnaire examQuestionnaire) {
        examQuestionnaireDao.save(examQuestionnaire);
    }

    @Override
    public ExamQuestionnaire getOneExamQuestionnaire(int id) {
        return examQuestionnaireDao.findExamQuestionnaireById(id);
    }

    @Override
    @Transactional
    public void save(ExamQuestion examQuestion) {
        examQuestionDao.save(examQuestion);
    }

    @Override
    public ExamQuestion getExamById(int id) {
        return examQuestionDao.findExamQuestionById(id);
    }

    @Override
    public IsWritten findWittenById(int id) {
        return writtenDao.getById(id);
    }
}
