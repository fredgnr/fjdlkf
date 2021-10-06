package com.example.backend.dao;

import com.example.backend.entity.IsWritten;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WrittenDao extends JpaRepository<IsWritten, Integer> {
    List<IsWritten> findAllByQuestionnaireId(int questionnaireId);

    int countAllByQuestionnaireIdAndUserId(int questionnaireId, int userId);

    void deleteById(int id);
}
