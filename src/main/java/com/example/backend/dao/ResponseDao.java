package com.example.backend.dao;

import com.example.backend.entity.Response;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResponseDao extends JpaRepository<Response, Integer> {
    int countAllByAnswerAndQuestionId(String answer, int questionId);

    List<Response> getAllByQuestionIdAndIsWrittenId(int questionId, int isWrittenId);
}
