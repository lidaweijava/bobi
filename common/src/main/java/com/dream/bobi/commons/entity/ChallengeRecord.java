package com.dream.bobi.commons.entity;

import java.util.Date;

public class ChallengeRecord extends BaseEntity {

    private Long userId;
    private Long challengeId;
    private Integer category;
    private Date challengeTime;
    private Integer status;
    private Integer dialogCount;
    private Character grade;

    //0：未知,1:50次未通过,2:放弃
    private Integer failCause;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Long challengeId) {
        this.challengeId = challengeId;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public Date getChallengeTime() {
        return challengeTime;
    }

    public void setChallengeTime(Date challengeTime) {
        this.challengeTime = challengeTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getDialogCount() {
        return dialogCount;
    }

    public void setDialogCount(Integer dialogCount) {
        this.dialogCount = dialogCount;
    }

    public Character getGrade() {
        return grade;
    }

    public void setGrade(Character grade) {
        this.grade = grade;
    }

    public Integer getFailCause() {
        return failCause;
    }

    public void setFailCause(Integer failCause) {
        this.failCause = failCause;
    }
}
