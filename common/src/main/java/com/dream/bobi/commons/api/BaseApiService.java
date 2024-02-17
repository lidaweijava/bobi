package com.dream.bobi.commons.api;

import com.dream.bobi.commons.constants.BaseApiConstants;
import com.dream.bobi.commons.enums.MsgCode;

import java.util.HashMap;
import java.util.Map;

/**
 * created on 2019/3/3 11:06
 *
 * @author nfboy_liusong@163.com
 * @version 1.0.0
 */

public class BaseApiService {

    /**
     * 返回错误
     *
     * @param msg
     * @return
     */
    public Map<String, Object> setResultError(String msg) {
        return setResult(BaseApiConstants.HTTP_500_CODE, msg, null);
    }
    public Map<String, Object> setResultError(int code,String msg) {
        return setResult(code, msg, null);
    }
   public Map<String, Object> setResultError(MsgCode msgCode) {
        return setResult(msgCode.getCode(),msgCode.getMessage(), null);
    }
   public Map<String, Object> setResultErrorWithData(MsgCode msgCode,String data) {
        return setResult(msgCode.getCode(),msgCode.getMessage(), data);
    }
   public Map<String, Object> setResultSuccess(MsgCode msgCode) {
        return setResult(msgCode.getCode(),msgCode.getMessage(), null);
    }
   public Map<String, Object> setResultSuccessWithData(MsgCode msgCode,String data) {
        return setResult(msgCode.getCode(),msgCode.getMessage(), data);
    }
    public Map<String, Object> setSystemError() {
        return setResult(MsgCode.SYS_ERROR.getCode(),MsgCode.SYS_ERROR.getMessage(), null);
    }

    /**
     * 返回成功
     *
     * @return
     */
    public Map<String, Object> setResultSuccess() {
        return setResult(BaseApiConstants.HTTP_200_CODE, BaseApiConstants.HTTP_SUCCESS_NAME, null);
    }

    /**
     * 返回成功
     *
     * @param msg
     * @return
     */
    public Map<String, Object> setResultSuccess(String msg) {
        return setResult(BaseApiConstants.HTTP_200_CODE, msg, null);
    }

    /**
     * 返回成功
     *
     * @param data
     * @return
     */
    public Map<String, Object> setResultSuccessData(Object data) {
        return setResult(BaseApiConstants.HTTP_200_CODE, BaseApiConstants.HTTP_SUCCESS_NAME, data);
    }

    /**
     * 功能描述:(返回失败)
     * @param msg
     * @return
     */
    public Map<String, Object> setResultParameterError(String msg) {
        return setResult(BaseApiConstants.HTTP_400_CODE, msg, null);
    }
    public Map<String, Object> setResultParameterError(MsgCode msgCode) {
        return setResult(msgCode.getCode(), msgCode.getMessage(), null);
    }

    /**
     * 自定义返回
     *
     * @param code
     * @param msg
     * @param data
     * @return
     */
    public Map<String, Object> setResult(Integer code, String msg, Object data) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(BaseApiConstants.HTTP_CODE_NAME, code);
        result.put(BaseApiConstants.HTTP_200_NAME, msg);
        if (data != null)
            result.put(BaseApiConstants.HTTP_DATA_NAME, data);
        return result;
    }

}
