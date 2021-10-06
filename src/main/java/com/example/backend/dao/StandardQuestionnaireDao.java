package com.example.backend.dao;


import com.example.backend.entity.StandardQuestionnaire;
import com.example.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StandardQuestionnaireDao extends JpaRepository<StandardQuestionnaire, Integer> {
    List<StandardQuestionnaire> findAllByUser(User user);

    StandardQuestionnaire findStandardQuestionnaireById(int id);
}
