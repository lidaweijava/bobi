package com.dream.bobi.commons.entity;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

@Table(name="training_code")
public class TrainingCode extends BaseEntity{

    @Column(name = "`code`")
    private String code;

    private Long userId;

    private Date useTime;
    private Integer status;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getUseTime() {
        return useTime;
    }

    public void setUseTime(Date useTime) {
        this.useTime = useTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
