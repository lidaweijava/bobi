package com.dream.bobi.controller;

import com.dream.bobi.commons.api.BaseApiService;
import com.dream.bobi.commons.entity.Config;
import com.dream.bobi.commons.mapper.ConfigMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sys")
public class SystemController  extends BaseApiService {
    private final static Logger log = LoggerFactory.getLogger(SystemController.class);

    @Autowired
    ConfigMapper configMapper;
    @GetMapping("/entries")
    public Map<String,Object> entries() {
        List<Config> configs = configMapper.selectAll();
        if(!CollectionUtils.isEmpty(configs)){
            return setResultSuccessData(configs);
        }else{
            return setResultSuccessData(null);
        }
    }
    @PostMapping("/addEntry")
    public Map<String,Object> addEntry(@RequestParam String text) {
        Config config = new Config();
        config.setText(text);
        try {
            configMapper.insert(config);
            return setResultSuccessData(null);
        }catch (Exception e){
            log.error("save config error text:{}", text,e);
            return setSystemError();
        }
    }
    @DeleteMapping("/entry/{configId}")
    public Map<String,Object> delEntry(@PathVariable Long configId) {
        try {
            Config c =new Config();
            c.setId(configId);
            configMapper.deleteByPrimaryKey(c);
            return setResultSuccessData(null);
        }catch (Exception e){
            log.error("deleteByPrimaryKey error id:{}", configId,e);
            return setSystemError();
        }
    }
}
