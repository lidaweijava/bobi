package com.dream.bobi.support;

import com.dream.bobi.commons.enums.MsgCode;

public class BizException extends RuntimeException{
    private MsgCode msgCode;

    public MsgCode getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(MsgCode msgCode) {
        this.msgCode = msgCode;
    }

    public BizException(MsgCode msgCode){
        this.msgCode = msgCode;
    }

}
