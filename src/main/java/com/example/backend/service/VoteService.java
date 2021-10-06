package com.example.backend.service;

import com.example.backend.entity.VoteQuestion;
import com.example.backend.entity.VoteQuestionnaire;


public interface VoteService {

    VoteQuestionnaire getOneVoteQuestionnaire(int id);

    void save(VoteQuestionnaire voteQuestionnaire);

    VoteQuestion getVoteById(int id);

    void save(VoteQuestion voteQuestion);
}



