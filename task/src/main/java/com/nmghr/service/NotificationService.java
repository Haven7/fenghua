package com.nmghr.service;

import com.nmghr.db.wrapper.HrQueryWrapper;
import com.nmghr.entity.TErrorReportEntity;
import com.nmghr.entity.TReportTimeEntity;
import com.nmghr.entity.TReportUserEntity;
import com.nmghr.lanxin.LanXinThread;
import com.nmghr.mapper.TErrorReportMapper;
import com.nmghr.mapper.TReportTimeMapper;
import com.nmghr.mapper.TReportUserMapper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class NotificationService {

    @Autowired
    private TReportTimeMapper tReportTimeMapper;

    @Autowired
    private TReportUserMapper tReportUserMapper;

    @Autowired
    private TErrorReportMapper tErrorReportMapper;

    @XxlJob("NotificationStatistics")
    private void NotificationStatistics() throws Exception {
        // 查询所有未统计且报备结束时间 < 当前时间 的
        HrQueryWrapper<TReportTimeEntity> hrQueryWrapper = new HrQueryWrapper<>();
        hrQueryWrapper.eq("sfsc","0").eq("sfqy","1").eq("bbsjzt","0");
        // todo 报备结束时间 < 当前时间
        hrQueryWrapper.apply("bbjssj < now()");
        List<TReportTimeEntity> list = tReportTimeMapper.selectList(hrQueryWrapper);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 查询所有报备时间 获取开始时间和结束时间   判断当前时间是否大于报备结束时间  大于则进行统计

        if (!ObjectUtils.isEmpty(list) && list.size() > 0){
            for (TReportTimeEntity entity : list){
                // 查询未报备的人  查询该机构人员主表 警号关联查是否有报备记录
                Map<String, Object> map = new HashMap<>();
                map.put("jgbm",entity.getJgbm());
                // todo 时间转换
                String kssj = dateFormat.format(new Date()) + " " + entity.getBbkssj() + ":00";
                String jssj = dateFormat.format(new Date()) + " " + entity.getBbjssj() + ":00";
                map.put("kssj",kssj);
                map.put("jssj",jssj);
                List<Map<String, Object>> userList = tReportUserMapper.queryErrorUser(map);

                // 循环保存
                if (!ObjectUtils.isEmpty(userList) && userList.size() > 0){
                    for (Map<String, Object> userMap : userList){
                        TErrorReportEntity errorReportEntity = new TErrorReportEntity();
                        errorReportEntity.setJh(userMap.get("jh").toString());
                        errorReportEntity.setXm(userMap.get("xm").toString());
                        errorReportEntity.setJgmc(userMap.get("jgmc").toString());
                        errorReportEntity.setJgbm(userMap.get("jgbm").toString());
                        errorReportEntity.setSjh(userMap.get("sjh").toString());
                        errorReportEntity.setYcbbsj(simpleDateFormat.parse(kssj));
                        errorReportEntity.setCjsj(new Date());
                        errorReportEntity.setSftb("0");
                        tErrorReportMapper.insert(errorReportEntity);
                    }
                }
                TReportTimeEntity tReportTimeEntity = new TReportTimeEntity();
                tReportTimeEntity.setId(entity.getId());
                tReportTimeEntity.setBbsjzt("1");
                tReportTimeMapper.updateById(tReportTimeEntity);
            }
        }

    }


    @XxlJob("NotificationStart")
    private void NotificationStart(){
        // 查询昨日未报备人员的机构
        Map<String, Object> params = new HashMap<>();
        Date today = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String yesterday = simpleDateFormat.format(today);//获取昨天日期

        params.put("kssj",yesterday + " 00:00:00");
        params.put("jssj",yesterday + " 23:59:59");
        List<String> list = tErrorReportMapper.queryJgbm(params);
        // 循环机构 查询未通报人员进行通报
        if (!ObjectUtils.isEmpty(list) && list.size() > 0){
            for (String jgbm : list){
                // 查询人
                HrQueryWrapper<TErrorReportEntity> hrQueryWrapper = new HrQueryWrapper<>();
                hrQueryWrapper.eq("jgbm",jgbm);
                hrQueryWrapper.eq("sftb","0");
                hrQueryWrapper.apply("cjsj >= STR_TO_DATE('" +yesterday + " 00:00:00" + "','%Y-%m-%d %H:%i:%s')");
                hrQueryWrapper.apply("cjsj <= STR_TO_DATE('" +yesterday + " 23:59:59" + "','%Y-%m-%d %H:%i:%s')");
                List<TErrorReportEntity> errorList = tErrorReportMapper.selectList(hrQueryWrapper);

                if (!ObjectUtils.isEmpty(errorList) && errorList.size() > 0){
                    String content = "";
                    for (TErrorReportEntity errorReportEntity : errorList){
                        TErrorReportEntity newErrorReportEntity = new TErrorReportEntity();
                        newErrorReportEntity.setId(errorReportEntity.getId());
                        newErrorReportEntity.setSftb("1");
                        tErrorReportMapper.updateById(newErrorReportEntity);
                        content += errorReportEntity.getXm() + ",";
                    }
                    content = content.substring(0,content.length() - 1);
                    remind(jgbm,content);
                }
            }
        }
    }

    public void remind(String jgbm, String content){
        HrQueryWrapper<TReportUserEntity> queryWrapper = new HrQueryWrapper<>();
        queryWrapper.eq("jgbm",jgbm);
        List<TReportUserEntity> list = tReportUserMapper.selectList(queryWrapper);

        List<Map<String, Object>> userList = new ArrayList<>();

        if (!ObjectUtils.isEmpty(list) && list.size() > 0){
            for (TReportUserEntity entity : list){
                Map<String, Object> lanxinParam = new HashMap<>();
                // 调用蓝信推送
                lanxinParam.put("phone", entity.getSjh());
                lanxinParam.put("title", "警力报备通报");
                lanxinParam.put("content", "昨日未按时报备人员：" + content);
                userList.add(lanxinParam);
            }
            Map<String, Object> lanxinmap = new HashMap<>();
            lanxinmap.put("list", userList);
            LanXinThread.add(lanxinmap);
        }
    }

    //查询报备时间 设置为未统计 每天凌晨执行一次

    @XxlJob("CancelStatistics")
    private void CancelStatistics(){
        TReportTimeEntity tReportTimeEntity = new TReportTimeEntity();
        tReportTimeEntity.setBbsjzt("0");
        HrQueryWrapper<TReportTimeEntity> hrQueryWrapper = new HrQueryWrapper<>();
        hrQueryWrapper.eq("bbsjzt","1");
        tReportTimeMapper.update(tReportTimeEntity,hrQueryWrapper);
    }
}
