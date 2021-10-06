package com.example.backend.dao;

import com.example.backend.entity.EpidemicQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EpidemicQuestionDao extends JpaRepository<EpidemicQuestion, Integer> {
    EpidemicQuestion findEpidemicById(int id);
}
