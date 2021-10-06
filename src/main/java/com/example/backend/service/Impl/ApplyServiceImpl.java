package com.example.backend.service.Impl;

import com.example.backend.dao.*;
import com.example.backend.entity.*;
import com.example.backend.service.ApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ApplyServiceImpl implements ApplyService {
    @Autowired
    ApplyQuestionnaireDao applyQuestionnaireDao;
    @Autowired
    ApplyQuestionDao applyQuestionDao;
    @Autowired
    WrittenDao writtenDao;
    @Autowired
    QuestionDao questionDao;
    @Autowired
    ResponseDao responseDao;

    @Override
    public ApplyQuestionnaire getOneApplyQuestionnaire(int id) {
        return applyQuestionnaireDao.findApplyQuestionnaireById(id);
    }

    @Override
    @Transactional
    public void save(ApplyQuestionnaire applyQuestionnaire) {
        applyQuestionnaireDao.save(applyQuestionnaire);
    }

    @Override
    public ApplyQuestion getApplyById(int id) {
        return applyQuestionDao.findApplyQuestionById(id);
    }

    @Override
    @Transactional
    public void save(ApplyQuestion applyQuestion) {
        applyQuestionDao.save(applyQuestion);
    }

    @Override
    @Transactional
    public void submitApplyAnswer(Questionnaire questionnaire, int userId, List<Map<String, Object>> answerList) throws Exception {
        IsWritten isWritten = new IsWritten();
        isWritten.setQuestionnaireId(questionnaire.getId());
        isWritten.setUserId(userId);
        isWritten.setSubmitTime(new Date());
        writtenDao.save(isWritten);
        for (Map<String, Object> answer : answerList) {
            int questionId = Integer.parseInt(answer.get("id").toString());
            ApplyQuestion question = applyQuestionDao.findApplyQuestionById(questionId);
            String options = answer.get("answer").toString();
            String type = answer.get("type").toString();
            List<Response> responses = question.getResponseList();
            if (type.equals("多选报名题") || type.equals("单选报名题")) {
                String[] numLimitArr = question.getLimitNum().split(",");
                String[] choiceArr = question.getChoices().split(",");//所有选项
                String[] optionArr = options.split(",");//用户的答案
                Map<String, Integer> map = new HashMap<>();
                for (int i = 0; i < numLimitArr.length; i++) {
                    System.out.println(choiceArr[i]);//for test
                    System.out.println(Integer.parseInt(numLimitArr[i]));//for test
                    map.put(choiceArr[i], Integer.parseInt(numLimitArr[i]));
                }
                for (String option : optionArr) {
                    System.out.println(responseDao.countAllByAnswerAndQuestionId(option, questionId));
                    if (responseDao.countAllByAnswerAndQuestionId(option, questionId) < map.get(option)) {
                        process(userId, isWritten, questionId, option, type, responses);
                    } else {
                        throw new Exception();
                    }
                }
            } else {
                process(userId, isWritten, questionId, options, type, responses);
            }
            question.setResponseList(responses);
            questionDao.save(question);
        }
    }

    public void process(int userId, IsWritten isWritten, int questionId, String options, String type, List<Response> responses) {
        Response response = new Response();
        response.setAnswer(options);
        response.setQuestionId(questionId);
        response.setType(type);
        response.setUserId(userId);
        response.setIsWrittenId(isWritten.getId());
        responses.add(response);
        responseDao.save(response);
    }
}
