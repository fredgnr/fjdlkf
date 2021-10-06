package com.example.backend.service;

import com.example.backend.entity.ApplyQuestion;
import com.example.backend.entity.ApplyQuestionnaire;
import com.example.backend.entity.Questionnaire;

import java.util.List;
import java.util.Map;

public interface ApplyService {
    ApplyQuestionnaire getOneApplyQuestionnaire(int id);

    void save(ApplyQuestionnaire applyQuestionnaire);

    ApplyQuestion getApplyById(int id);

    void save(ApplyQuestion applyQuestion);

    void submitApplyAnswer(Questionnaire questionnaire, int userId, List<Map<String, Object>> answerList) throws Exception;
}
