package com.example.backend.dao;


import com.example.backend.entity.EpidemicQuestionnaire;
import com.example.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EpidemicQuestionnaireDao extends JpaRepository<EpidemicQuestionnaire, Integer> {
    List<EpidemicQuestionnaire> findAllByUser(User user);

    EpidemicQuestionnaire findEpidemicQuestionnaireById(int id);
}
