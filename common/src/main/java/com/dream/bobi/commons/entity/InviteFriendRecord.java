package com.dream.bobi.commons.entity;

import javax.persistence.Table;

@Table(name="invite_friend_record")
public class InviteFriendRecord extends BaseEntity {

    private Long userId;

    private Long targetUserId;

    /**
     * 0-邀请，1-同意邀请
     */
    private Integer status;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId){
        this.userId = userId;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
