package com.example.backend.service.Impl;

import com.example.backend.dao.QuestionnaireDao;
import com.example.backend.dao.UserDao;
import com.example.backend.entity.Questionnaire;
import com.example.backend.entity.User;
import com.example.backend.service.QuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class QuestionnaireServiceImpl implements QuestionnaireService {
    @Autowired
    QuestionnaireDao questionnaireDao;
    @Autowired
    UserDao userDao;

    @Transactional
    public List<Questionnaire> getAllQuestionnaire(int userId) throws RuntimeException {
        List<Questionnaire> ret;
        User user = userDao.findUserById(userId);
        if (user == null) throw new RuntimeException("user not found");
        else {
            ret = questionnaireDao.findAllByUser(userId);
        }
        return ret;
    }

    public Questionnaire findQuestionnaireById(int questionnaireId) {
        return questionnaireDao.getById(questionnaireId);
    }


    public void save(Questionnaire questionnaire) {
        questionnaireDao.save(questionnaire);
    }

    @Transactional
    public void delete(Questionnaire questionnaire) {
        questionnaireDao.delete(questionnaire);
    }

    public int getCopyCount(int fid) {
        return questionnaireDao.countByFatherId(fid);
    }

}
