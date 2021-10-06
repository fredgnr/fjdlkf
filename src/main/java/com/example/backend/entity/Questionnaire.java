package com.example.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class Questionnaire {
    @OneToMany
    List<ApplyQuestion> applyQuestions;
    @OneToMany
    List<ExamQuestion> examQuestions;
    @OneToMany
    List<StandardQuestion> standardQuestions;
    @OneToMany
    List<VoteQuestion> voteQuestions;
    @OneToMany
    List<EpidemicQuestion> epidemicQuestions;
    @Id
    @GeneratedValue
    private int id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private User user;
    private int status;             //0未发布,1发布中,2已关闭
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;         //创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date issueTime;         //发布时间
    private int place = 0;            //0普通,1回收站,2收藏
    private String title;           //问卷标题
    private String description;     //问卷描述
    private int count = 0;            //获取该问卷的填写人数
    private String paperType;       //"投票","报名","调查","考试","疫情"
    private int fatherId = id;
    private boolean isSingle;
    private boolean showSeq;

    public List<EpidemicQuestion> getEpidemicQuestions() {
        return epidemicQuestions;
    }

    public void setEpidemicQuestions(List<EpidemicQuestion> epidemicQuestions) {
        this.epidemicQuestions = epidemicQuestions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getPaperType() {
        return paperType;
    }

    public void setPaperType(String paperType) {
        this.paperType = paperType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ApplyQuestion> getApplyQuestions() {
        return applyQuestions;
    }

    public void setApplyQuestions(List<ApplyQuestion> applyQuestions) {
        this.applyQuestions = applyQuestions;
    }

    public List<ExamQuestion> getExamQuestions() {
        return examQuestions;
    }

    public void setExamQuestions(List<ExamQuestion> examQuestions) {
        this.examQuestions = examQuestions;
    }

    public List<StandardQuestion> getStandardQuestions() {
        return standardQuestions;
    }

    public void setStandardQuestions(List<StandardQuestion> standardQuestions) {
        this.standardQuestions = standardQuestions;
    }

    public List<VoteQuestion> getVoteQuestions() {
        return voteQuestions;
    }

    public void setVoteQuestions(List<VoteQuestion> voteQuestions) {
        this.voteQuestions = voteQuestions;
    }

    public int getFatherId() {
        return fatherId;
    }

    public void setFatherId(int fatherId) {
        this.fatherId = fatherId;
    }

    public boolean isSingle() {
        return isSingle;
    }

    public void setSingle(boolean single) {
        isSingle = single;
    }

    public boolean isShowSeq() {
        return showSeq;
    }

    public void setShowSeq(boolean showSeq) {
        this.showSeq = showSeq;
    }

    public Date getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(Date issueTime) {
        this.issueTime = issueTime;
    }
}
