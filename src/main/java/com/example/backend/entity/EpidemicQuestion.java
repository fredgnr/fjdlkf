package com.example.backend.entity;

import javax.persistence.Entity;

@Entity
public class EpidemicQuestion extends Question {
    private boolean visible;    //报名人数是否可见

    public EpidemicQuestion() {
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
