package com.example.backend.service;

import com.example.backend.entity.ExamQuestion;
import com.example.backend.entity.ExamQuestionnaire;
import com.example.backend.entity.IsWritten;

public interface ExamService {
    ExamQuestionnaire getOneExamQuestionnaire(int id);

    void save(ExamQuestion examQuestion);

    ExamQuestion getExamById(int id);

    void save(ExamQuestionnaire examQuestionnaire);

    IsWritten findWittenById(int id);
}
