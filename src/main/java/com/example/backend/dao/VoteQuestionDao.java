package com.example.backend.dao;

import com.example.backend.entity.VoteQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteQuestionDao extends JpaRepository<VoteQuestion, Integer> {
    VoteQuestion findVoteById(int id);
}
