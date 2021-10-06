package com.example.backend.dao;

import com.example.backend.entity.ExamQuestionnaire;
import com.example.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamQuestionnaireDao extends JpaRepository<ExamQuestionnaire, Integer> {
    List<ExamQuestionnaire> findAllByUser(User user);

    ExamQuestionnaire findExamQuestionnaireById(int id);
}
