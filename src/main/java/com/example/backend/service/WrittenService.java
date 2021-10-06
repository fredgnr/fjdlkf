package com.example.backend.service;

import com.example.backend.entity.IsWritten;

import java.util.List;

public interface WrittenService {
    void save(IsWritten isWritten);

    List<IsWritten> getAllByQuestionnaire(int questionnaireId);

    int countIsWritten(int questionnaireId, int userId);

    //    boolean saveApplyQuestionnaire(int questionnaireId, int userId);
    void deleteOneIsWritten(int id);
}
