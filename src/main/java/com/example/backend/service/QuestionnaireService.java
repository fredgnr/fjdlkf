package com.example.backend.service;

import com.example.backend.entity.Questionnaire;

import java.util.List;

public interface QuestionnaireService {
    List<Questionnaire> getAllQuestionnaire(int userId) throws RuntimeException;

    Questionnaire findQuestionnaireById(int questionnaireId);

    void save(Questionnaire questionnaire);

    void delete(Questionnaire questionnaire);

    int getCopyCount(int fid);
}
