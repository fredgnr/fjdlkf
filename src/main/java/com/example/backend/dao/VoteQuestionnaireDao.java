package com.example.backend.dao;

import com.example.backend.entity.User;
import com.example.backend.entity.VoteQuestionnaire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoteQuestionnaireDao extends JpaRepository<VoteQuestionnaire, Integer> {
    List<VoteQuestionnaire> findAllByUser(User user);

    VoteQuestionnaire findVoteQuestionnaireById(int id);
}
