package com.example.backend.entity;

import javax.persistence.Entity;

@Entity
public class ApplyQuestion extends Question {
    private String limitNum;                        //选项限制人数
    private boolean visible;                        //报名人数是否可见

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getLimitNum() {
        return limitNum;
    }

    public void setLimitNum(String limitNum) {
        this.limitNum = limitNum;
    }

}
