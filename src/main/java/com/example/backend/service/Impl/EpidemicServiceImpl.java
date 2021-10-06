package com.example.backend.service.Impl;

import com.example.backend.dao.EpidemicQuestionDao;
import com.example.backend.dao.EpidemicQuestionnaireDao;
import com.example.backend.entity.EpidemicQuestion;
import com.example.backend.entity.EpidemicQuestionnaire;
import com.example.backend.service.EpidemicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class EpidemicServiceImpl implements EpidemicService {
    @Autowired
    EpidemicQuestionnaireDao epidemicQuestionnaireDao;
    @Autowired
    EpidemicQuestionDao epidemicQuestionDao;

    public EpidemicQuestionnaire getOneEpidemicQuestionnaire(int id) {
        return epidemicQuestionnaireDao.findEpidemicQuestionnaireById(id);
    }

    @Transactional
    public void save(EpidemicQuestionnaire epidemicQuestionnaire) {
        epidemicQuestionnaireDao.save(epidemicQuestionnaire);
    }

    public EpidemicQuestion getEpidemicById(int id) {
        return epidemicQuestionDao.findEpidemicById(id);
    }

    @Transactional
    public void save(EpidemicQuestion epidemicQuestion) {
        epidemicQuestionDao.save(epidemicQuestion);
    }
}
