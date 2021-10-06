package com.example.backend.dao;

import com.example.backend.entity.ApplyQuestionnaire;
import com.example.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplyQuestionnaireDao extends JpaRepository<ApplyQuestionnaire, Integer> {
    List<ApplyQuestionnaire> findAllByUser(User user);

    ApplyQuestionnaire findApplyQuestionnaireById(int id);
}
