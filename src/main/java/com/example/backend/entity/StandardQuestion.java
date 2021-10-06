package com.example.backend.entity;

import javax.persistence.Entity;

@Entity
public class StandardQuestion extends Question {
    private boolean visible;

    public StandardQuestion() {
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
