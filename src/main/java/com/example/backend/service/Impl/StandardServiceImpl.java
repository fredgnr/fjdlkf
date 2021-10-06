package com.example.backend.service.Impl;

import com.example.backend.dao.StandardQuestionDao;
import com.example.backend.dao.StandardQuestionnaireDao;
import com.example.backend.entity.StandardQuestion;
import com.example.backend.entity.StandardQuestionnaire;
import com.example.backend.service.StandardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StandardServiceImpl implements StandardService {
    @Autowired
    StandardQuestionnaireDao standardQuestionnaireDao;
    @Autowired
    StandardQuestionDao standardQuestionDao;

    public StandardQuestionnaire getOneStandardQuestionnaire(int id) {
        return standardQuestionnaireDao.findStandardQuestionnaireById(id);
    }

    @Transactional
    public void save(StandardQuestionnaire standardQuestionnaire) {
        standardQuestionnaireDao.save(standardQuestionnaire);
    }

    public StandardQuestion getStandardById(int id) {
        return standardQuestionDao.findStandardById(id);
    }

    @Transactional
    public void save(StandardQuestion standardQuestion) {
        standardQuestionDao.save(standardQuestion);
    }
}
