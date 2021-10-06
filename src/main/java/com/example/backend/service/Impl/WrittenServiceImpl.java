package com.example.backend.service.Impl;

import com.example.backend.dao.WrittenDao;
import com.example.backend.entity.IsWritten;
import com.example.backend.service.ApplyService;
import com.example.backend.service.WrittenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WrittenServiceImpl implements WrittenService {

    @Autowired
    WrittenDao writtenDao;

    @Autowired
    ApplyService applyService;

    @Override
    @Transactional
    public void save(IsWritten isWritten) {
        writtenDao.save(isWritten);
    }

    @Override
    public List<IsWritten> getAllByQuestionnaire(int questionnaireId) {
        return writtenDao.findAllByQuestionnaireId(questionnaireId);
    }

    @Override
    public int countIsWritten(int questionnaireId, int userId) {
        return writtenDao.countAllByQuestionnaireIdAndUserId(questionnaireId, userId);
    }

    @Override
    public void deleteOneIsWritten(int id) {
        writtenDao.deleteById(id);
    }

}
