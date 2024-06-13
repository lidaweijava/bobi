package com.dream.bobi.controller;

import com.dream.bobi.commons.api.BaseApiService;
import com.dream.bobi.commons.entity.UploadRecord;
import com.dream.bobi.commons.entity.UserEntity;
import com.dream.bobi.commons.enums.MsgCode;
import com.dream.bobi.commons.mapper.UploadRecordMapper;
import com.dream.bobi.manage.TencentCosTools;
import com.dream.bobi.manage.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@RestController
public class UploadController extends BaseApiService {
    private final static Logger log = LoggerFactory.getLogger(UploadController.class);

    @Autowired
    UserService userService;

    @Autowired
    UploadRecordMapper uploadRecordMapper;
    @PostMapping("/training/report/upload")
    public Map<String, Object> upload(@RequestParam String token, MultipartFile file){
        UserEntity user = userService.getUser(token);
        try {
            String name = TencentCosTools.upload(file);
            UploadRecord uploadRecord = new UploadRecord();
            uploadRecord.setUri(name);
            uploadRecord.setUserId(user.getId());
            uploadRecordMapper.insertSelective(uploadRecord);
            // save upload record
            return setResultSuccess(name);
        } catch (Exception e) {
            log.error("upload error ", e);
            return setResultError(MsgCode.UPLOAD_FAILED);
        }
    }
    @GetMapping("/upload")
    public Map<String, Object> upload(MultipartFile file){
        try {
            String name = TencentCosTools.upload(file);
            return setResultSuccess(name);
        } catch (Exception e) {
            log.error("upload error ", e);
            return setResultError(MsgCode.UPLOAD_FAILED);
        }
    }
    @PostMapping("/training/report/delete")
    public Map<String, Object> deleteRecord(@RequestParam String token,@RequestParam Long id){
        try {
            userService.getUser(token);
            UploadRecord uploadRecord = new UploadRecord();
            uploadRecord.setId(id);
            uploadRecordMapper.deleteByPrimaryKey(uploadRecord);
            return setResultSuccess();
        } catch (Exception e) {
            log.error("deleteRecord error ", e);
            return setResultError(MsgCode.UPLOAD_FAILED);
        }
    }
    @GetMapping("/training/report/list")
    public Map<String, Object> list(@RequestParam String token){
        try {
            UserEntity user = userService.getUser(token);
            Example example = new Example(UploadRecord.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("userId",user.getId());
            example.setOrderByClause("id desc limit 50");
            List<UploadRecord> uploadRecords = uploadRecordMapper.selectByExample(example);
            // save upload record
            return setResultSuccessData(uploadRecords);
        } catch (Exception e) {
            log.error("list error ", e);
            return setResultError(MsgCode.SYS_ERROR);
        }
    }
}
