package com.example.backend.entity;

import javax.persistence.Entity;

@Entity
public class ExamQuestion extends Question {
    private String type;    //"选择","填空"
    private int score;      //题目的分值
    private String correctAnswer;  //标答,多选题的标答记为“A,B"

    public ExamQuestion() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
