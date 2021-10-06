package com.example.backend.entity;

import javax.persistence.Entity;

@Entity
public class ExamQuestionnaire extends Questionnaire {
    private boolean canSeeScore;
    private int totalScore;
    private int duration;//考试的持续时间

    public boolean isCanSeeScore() {
        return canSeeScore;
    }

    public void setCanSeeScore(boolean canSeeScore) {
        this.canSeeScore = canSeeScore;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
