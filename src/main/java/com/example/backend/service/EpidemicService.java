package com.example.backend.service;

import com.example.backend.entity.EpidemicQuestion;
import com.example.backend.entity.EpidemicQuestionnaire;

public interface EpidemicService {
    EpidemicQuestionnaire getOneEpidemicQuestionnaire(int id);

    void save(EpidemicQuestionnaire epidemicQuestionnaire);

    EpidemicQuestion getEpidemicById(int id);

    void save(EpidemicQuestion epidemicQuestion);
}
