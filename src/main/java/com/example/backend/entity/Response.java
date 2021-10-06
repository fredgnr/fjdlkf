package com.example.backend.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Response {
    @Id
    @GeneratedValue
    private int id;
    private String type;//填空题，多选题，单选题
    private String answer;//填空题是填的内容，单选题是选项内容等
    private int questionId;
    private int userId;
    private int isWrittenId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getIsWrittenId() {
        return isWrittenId;
    }

    public void setIsWrittenId(int isWrittenId) {
        this.isWrittenId = isWrittenId;
    }
}
