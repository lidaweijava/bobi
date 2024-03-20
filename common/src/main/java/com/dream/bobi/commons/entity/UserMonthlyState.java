package com.dream.bobi.commons.entity;


public class UserMonthlyState extends BaseEntity {

    private Long userId;
    private String month;
    private Long chatStateBit;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Long getChatStateBit() {
        return chatStateBit;
    }

    public void setChatStateBit(Long chatStateBit) {
        this.chatStateBit = chatStateBit;
    }
}
