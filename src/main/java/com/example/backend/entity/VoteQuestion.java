package com.example.backend.entity;


import javax.persistence.Entity;

@Entity
public class VoteQuestion extends Question {
    private boolean visible;

    public VoteQuestion() {
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }


}
