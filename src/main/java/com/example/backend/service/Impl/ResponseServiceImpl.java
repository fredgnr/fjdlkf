package com.example.backend.service.Impl;

import com.example.backend.dao.ResponseDao;
import com.example.backend.entity.Response;
import com.example.backend.service.ResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResponseServiceImpl implements ResponseService {
    @Autowired
    ResponseDao responseDao;

    @Override
    @Transactional
    public void save(Response response) {
        responseDao.save(response);
    }

    @Override
    public int countByOption(String option, int questionId) {
        return responseDao.countAllByAnswerAndQuestionId(option, questionId);
    }

    @Override
    public List<Response> getByQuestionAndIsWritten(int questionId, int isWrittenId) {
        return responseDao.getAllByQuestionIdAndIsWrittenId(questionId, isWrittenId);
    }

}
