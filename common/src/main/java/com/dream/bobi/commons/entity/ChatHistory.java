package com.dream.bobi.commons.entity;


import javax.persistence.Table;

@Table(name="chat_history")
public class ChatHistory  extends BaseEntity{
    private String content;

    private Integer side;

    private Long userId;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getSide() {
        return side;
    }

    public void setSide(Integer side) {
        this.side = side;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
