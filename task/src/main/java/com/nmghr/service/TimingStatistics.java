package com.nmghr.service;

import com.nmghr.common.Result;
import com.nmghr.mapper.StatisticsMapper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.apache.velocity.runtime.directive.Break;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author HavenF
 */
@Component
public class TimingStatistics {

    @Autowired
    StatisticsMapper statisticsMapper;

    @Autowired
    TReportRecordService tReportRecordService;


    //定时任务统计
    @XxlJob("TimingStatistics")
    private void timingStatistics() throws ParseException {
        List< Map<String, Object>> jgList = statisticsMapper.queryJgList();
        for(Map<String, Object> jgMap :jgList){
            jgMap.put("deptCode",jgMap.get("jgbm"));
            int carNum = 0;
            int perNum = 0;
            Map<String, Object> resultMap = new HashMap<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd ");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
            SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String day = sdf.format(new Date());
            String now = sdf2.format(new Date());
            String tjkssj = day+jgMap.get("tjkssj");
            String tjjssj = day+jgMap.get("tjjssj");
            jgMap.put("tjkssj", tjkssj);
            if("1".equals(jgMap.get("sfkt").toString())){
                Date date = null ;
                date = sdf3.parse(tjjssj);
                date.setTime(date.getTime() + 24*60*60*1000);
                tjjssj = sdf2.format(date);
                jgMap.put("tjjssj",tjjssj);
            }else {
                jgMap.put("tjjssj",day+jgMap.get("tjjssj"));
            }
            jgMap.put("bbfl","1");
            carNum =  statisticsMapper.statisticsReport(jgMap);
            jgMap.put("bbfl","0");
            perNum =  statisticsMapper.statisticsReport(jgMap);
            resultMap.put("cs",carNum);
            resultMap.put("rs",perNum);
            resultMap.put("jgmc",jgMap.get("jgmc"));
            resultMap.put("jgbm",jgMap.get("jgbm"));
            resultMap.put("tjkssj",jgMap.get("tjkssj"));
            resultMap.put("tjjssj",jgMap.get("tjjssj"));
            resultMap.put("cjsj", now);
            resultMap.put("xgsj", now);
            statisticsMapper.saveStatistics(resultMap);

        }
    }
}
