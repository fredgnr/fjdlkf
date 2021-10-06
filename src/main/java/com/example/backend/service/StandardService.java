package com.example.backend.service;

import com.example.backend.entity.StandardQuestion;
import com.example.backend.entity.StandardQuestionnaire;

public interface StandardService {
    StandardQuestionnaire getOneStandardQuestionnaire(int id);

    void save(StandardQuestionnaire standardQuestionnaire);

    StandardQuestion getStandardById(int id);

    void save(StandardQuestion standardQuestion);
}
