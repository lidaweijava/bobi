package com.dream.bobi.commons.entity;

public class Challenge extends BaseEntity {
    private String title;
    private Integer category;
    private String background;
    private String target;
    private String prologue;
    private String aiDetail;
    private String sampleDialog;
    private Integer maxTimes;
    private String successKeyword;
    private String failKeyword;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getPrologue() {
        return prologue;
    }

    public void setPrologue(String prologue) {
        this.prologue = prologue;
    }

    public String getAiDetail() {
        return aiDetail;
    }

    public void setAiDetail(String aiDetail) {
        this.aiDetail = aiDetail;
    }

    public String getSampleDialog() {
        return sampleDialog;
    }

    public void setSampleDialog(String sampleDialog) {
        this.sampleDialog = sampleDialog;
    }

    public Integer getMaxTimes() {
        return maxTimes;
    }

    public void setMaxTimes(Integer maxTimes) {
        this.maxTimes = maxTimes;
    }

    public String getSuccessKeyword() {
        return successKeyword;
    }

    public void setSuccessKeyword(String successKeyword) {
        this.successKeyword = successKeyword;
    }

    public String getFailKeyword() {
        return failKeyword;
    }

    public void setFailKeyword(String failKeyword) {
        this.failKeyword = failKeyword;
    }
}
