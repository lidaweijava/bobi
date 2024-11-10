package com.dream.bobi.commons.enums;

public enum InviteFriendStatus {

    INVITE(0, "邀请"),
    AGREE(1, "同意邀请"),
    ;

    int status;
    String message;

    InviteFriendStatus(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }
}
