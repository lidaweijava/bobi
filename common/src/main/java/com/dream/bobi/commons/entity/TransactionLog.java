package com.dream.bobi.commons.entity;

import javax.persistence.Column;
import java.util.Date;

public class TransactionLog extends BaseEntity {
    private Long userId;
    private Integer balance;

    @Column(name = "`change`")
    private Integer change;
    private Integer type;
    private Date transTime;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Integer getChange() {
        return change;
    }

    public void setChange(Integer change) {
        this.change = change;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getTransTime() {
        return transTime;
    }

    public void setTransTime(Date transTime) {
        this.transTime = transTime;
    }
}
