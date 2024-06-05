package com.dream.bobi.controller;


import com.dream.bobi.commons.api.BaseApiService;
import com.dream.bobi.commons.entity.*;
import com.dream.bobi.commons.enums.MsgCode;
import com.dream.bobi.commons.mapper.BannerMapper;
import com.dream.bobi.commons.mapper.TrainingCodeMapper;
import com.dream.bobi.commons.mapper.TrainingRecordMapper;
import com.dream.bobi.commons.mapper.UserMapper;
import com.dream.bobi.commons.utils.CustomEncryptor;
import com.dream.bobi.commons.utils.DateUtils;
import com.dream.bobi.manage.CacheService;
import com.dream.bobi.manage.UserService;
import com.dream.bobi.support.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@RestController
public class TrainingController extends BaseApiService {
    private final static Logger log = LoggerFactory.getLogger(TrainingController.class);

    @Autowired
    UserService userService;

    @Autowired
    TrainingRecordMapper trainingRecordMapper;
    @Autowired
    TrainingCodeMapper trainingCodeMapper;

    @Autowired
    UserMapper userMapper;
    @Autowired
    BannerMapper bannerMapper;

    @PostMapping("/training/record")
    public Map<String, Object> record(TrainingReportRequest reportRequest) {
        try {
            UserEntity user = userService.getUser(reportRequest.getToken());
            TrainingRecord trainingRecord = new TrainingRecord();
            trainingRecord.setTrainingCode(reportRequest.getTrainingCode());
            trainingRecord.setUserId(user.getId());
            trainingRecord.setTake(reportRequest.getTake());
            trainingRecord.setDay(DateUtils.currentDayYYYYMMDD());
            trainingRecord.setMonth(DateUtils.currentMonthYYYYMM());
            trainingRecord.setAward(reportRequest.getTake());
            trainingRecord.setStartTime(reportRequest.getStartTime());
            trainingRecord.setEndTime(reportRequest.getEndTime());
            trainingRecordMapper.insertSelective(trainingRecord);
            userService.recharge(user.getId(), Long.valueOf(reportRequest.getTake()));
            return setResultSuccessData(null);
        } catch (BizException e) {
            log.error("report error {}", e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        } catch (Exception e) {
            log.error("report error ", e);
            return setSystemError();
        }
    }

    @GetMapping("/training/monthList")
    public Map<String, Object> monthList(@RequestParam String token) {
        try {
            UserEntity user = userService.getUser(token);
            List<Integer> months = trainingRecordMapper.selectMonthList(user.getId());
            return setResultSuccessData(months);
        } catch (BizException e) {
            log.error("monthList error {}", e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        } catch (Exception e) {
            log.error("monthList error ", e);
            return setSystemError();
        }
    }

    @GetMapping("/training/dayCountByMonth")
    public Map<String, Object> dayCountByMonth(@RequestParam String token, @RequestParam Integer month) {
        try {
            UserEntity user = userService.getUser(token);
            List<DayCount> dayCounts = trainingRecordMapper.sumTakesGroupByDayPerMonth(user.getId(), month);
            return setResultSuccessData(dayCounts);
        } catch (BizException e) {
            log.error("dayCountByMonth error {}", e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        } catch (Exception e) {
            log.error("dayCountByMonth error ", e);
            return setSystemError();
        }
    }

    @PostMapping("/training/useCode")
    public Map<String, Object> useCode(@RequestParam String token, @RequestParam String code) {
        try {
            UserEntity user = userService.getUser(token);
            Example example = new Example(TrainingCode.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("code", code);
            TrainingCode exist = trainingCodeMapper.selectOneByExample(example);
            if (exist == null) {
                return setResultError(MsgCode.INVALID_CODE);
            }
            if (exist.getStatus() == 1) {
                return setResultError(MsgCode.CODE_HAS_BEEN_USED);
            }
            TrainingCode trainingCode4Update = new TrainingCode();
            trainingCode4Update.setCode(code);
            trainingCode4Update.setStatus(1);
            trainingCode4Update.setUseTime(new Date());
            trainingCode4Update.setUserId(user.getId());
            trainingCode4Update.setId(exist.getId());
            try {
                trainingCodeMapper.updateByPrimaryKeySelective(trainingCode4Update);
            } catch (Exception e) {
                return setResultError(MsgCode.CODE_USED_EXCEED_LIMIT);
            }
            User userInDB = new User();
            userInDB.setId(user.getId());
            userInDB.setVip(true);
            userInDB.setVipEndTime(DateUtils.tomorrowEndTime());
            userMapper.updateByPrimaryKeySelective(userInDB);
            return setResultSuccessData(null);
        } catch (BizException e) {
            log.error("useCode error {}", e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        } catch (Exception e) {
            log.error("useCode error ", e);
            return setSystemError();
        }

    }

    @GetMapping("/training/exportCodes")
    public Map<String, Object> exportCodes(@RequestParam Integer count) {
        if (count < 0 || count > 1000) {
            return setResultError(MsgCode.SYS_PARAM_ERROR);
        }
        int from = 1;
        Example example = new Example(TrainingCode.class);
        example.setOrderByClause("id desc limit 1");
        TrainingCode trainingCode = trainingCodeMapper.selectOneByExample(example);
        if (trainingCode != null) {
            from = Math.toIntExact(trainingCode.getId())+1;
        }
        try {
            List<TrainingCode> trainingCodes = new ArrayList<>();
            List<String> returnCodes = new ArrayList<>();
            for (int i = from; i <= count+from; i++) {
                String s = CustomEncryptor.encryptInt(i);
                TrainingCode c = new TrainingCode();
                c.setId((long) i);
                c.setCode(s);
                c.setStatus(0);
                trainingCodes.add(c);
                returnCodes.add(s);
            }
            trainingCodeMapper.insertList(trainingCodes);
            return setResultSuccessData(returnCodes);
        } catch (BizException e) {
            log.error("exportCodes error {}", e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        } catch (Exception e) {
            log.error("exportCodes error ", e);
            return setSystemError();
        }
    }

    @GetMapping("/training/bannerList")
    public Map<String, Object> bannerList() {

        try {
            List<Banner> bannersInCache = (List<Banner>) CacheService.configs.getIfPresent("banners");
            if(!CollectionUtils.isEmpty(bannersInCache)){
                return setResultSuccessData(bannersInCache);
            }
            List<Banner> banners = bannerMapper.selectAll();
            CacheService.configs.put("banners",banners);
            return setResultSuccessData(banners);
        } catch (BizException e) {
            log.error("bannerList error {}", e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        } catch (Exception e) {
            log.error("bannerList error ", e);
            return setSystemError();
        }
    }
    @PutMapping("/training/addBanner")
    public Map<String, Object> addBanner(BannerRequest bannerRequest) {
        try {
            Banner banner = new Banner();
            banner.setSchema(bannerRequest.getSchema());
            banner.setUri(bannerRequest.getUri());
            banner.setType(bannerRequest.getType());
            bannerMapper.insertSelective(banner);
            CacheService.configs.put("banners",Collections.emptyList());
            return setResultSuccessData(null);
        } catch (BizException e) {
            log.error("addBanner error {}", e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        } catch (Exception e) {
            log.error("addBanner error ", e);
            return setSystemError();
        }
    }
    @DeleteMapping("/training/delBanner/{id}")
    public Map<String, Object> delBanner(@PathVariable Long id) {
        try {
            Banner banner = new Banner();
            banner.setId(id);
            bannerMapper.deleteByPrimaryKey(banner);
            CacheService.configs.put("banners",Collections.emptyList());
            return setResultSuccessData(null);
        } catch (BizException e) {
            log.error("delBanner error {}", e.getMsgCode().getMessage());
            return setResultError(e.getMsgCode());
        } catch (Exception e) {
            log.error("delBanner error ", e);
            return setSystemError();
        }
    }

}
