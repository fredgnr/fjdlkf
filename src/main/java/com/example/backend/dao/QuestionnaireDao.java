package com.example.backend.dao;

import com.example.backend.entity.Questionnaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionnaireDao extends JpaRepository<Questionnaire, Integer> {
    @Query(value = "select * from questionnaire where user_id = ?1 ", nativeQuery = true)
    List<Questionnaire> findAllByUser(int userId);

    int countByFatherId(int fatherId);
}
