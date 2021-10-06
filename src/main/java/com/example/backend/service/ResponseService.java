package com.example.backend.service;

import com.example.backend.entity.Response;

import java.util.List;

public interface ResponseService {
    void save(Response response);

    int countByOption(String option, int questionId);

    List<Response> getByQuestionAndIsWritten(int questionId, int isWrittenId);
}
