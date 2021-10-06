package com.example.backend.service.Impl;

import com.example.backend.dao.VoteQuestionDao;
import com.example.backend.dao.VoteQuestionnaireDao;
import com.example.backend.entity.VoteQuestion;
import com.example.backend.entity.VoteQuestionnaire;
import com.example.backend.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VoteServiceImpl implements VoteService {
    @Autowired
    VoteQuestionnaireDao voteQuestionnaireDao;
    @Autowired
    VoteQuestionDao voteQuestionDao;

    public VoteQuestionnaire getOneVoteQuestionnaire(int id) {
        return voteQuestionnaireDao.findVoteQuestionnaireById(id);
    }

    @Transactional
    public void save(VoteQuestionnaire voteQuestionnaire) {
        voteQuestionnaireDao.save(voteQuestionnaire);
    }

    public VoteQuestion getVoteById(int id) {
        return voteQuestionDao.findVoteById(id);
    }

    @Transactional
    public void save(VoteQuestion voteQuestion) {
        voteQuestionDao.save(voteQuestion);
    }
}
