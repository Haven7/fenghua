package com.nmghr.service;

import com.nmghr.db.wrapper.HrQueryWrapper;
import com.nmghr.entity.TReportTimeEntity;
import com.nmghr.entity.TReportTimeExtendEntity;
import com.nmghr.entity.TReportUserEntity;
import com.nmghr.lanxin.LanXinThread;
import com.nmghr.mapper.TReportTimeExtendMapper;
import com.nmghr.mapper.TReportTimeMapper;
import com.nmghr.mapper.TReportUserMapper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class RemindService {

    @Autowired
    private TReportTimeMapper tReportTimeMapper;

    @Autowired
    private TReportTimeExtendMapper tReportTimeExtendMapper;

    @Autowired
    private TReportUserMapper tReportUserMapper;

    @XxlJob("RemindStart")
    private void remindStart() throws Exception {
        // 查询所有未删除，已启用的报备时间
        HrQueryWrapper<TReportTimeEntity> hrQueryWrapper = new HrQueryWrapper<>();
        hrQueryWrapper.eq("sfsc","0").eq("sfqy","1");
        List<TReportTimeEntity> list = tReportTimeMapper.selectList(hrQueryWrapper);

        DateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //循环
        if (!ObjectUtils.isEmpty(list) && list.size() > 0){
            for (TReportTimeEntity entity: list){
                //判断是否设置了提醒时间
                if (!"0".equals(entity.getTxsj())){
                    // 判断是否需要提醒；开始时间-提醒时间 <= 当前时间 需要进行提醒
                    if (isRemind(entity)){
                        //根据提醒频次 计算出需要提醒的时间 提醒时刻<= 当前时间 需要进行提醒

                        //查询提醒记录 判断是否已进行提醒
                        HrQueryWrapper<TReportTimeExtendEntity> queryWrapper = new HrQueryWrapper<>();
                        queryWrapper.eq("report_time_id",entity.getId());
                        List<TReportTimeExtendEntity> tReportTimeExtendEntityList = tReportTimeExtendMapper.selectList(queryWrapper);

                        if (!ObjectUtils.isEmpty(tReportTimeExtendEntityList) && tReportTimeExtendEntityList.size() > 0){
                            // 如果已提醒，查看提醒时间 <= 当前时间

                            //判断是否已提醒完 已结束
                            if ("0".equals(tReportTimeExtendEntityList.get(0).getSfjs())){
                                //提醒时间 <= 当前时间
                                if (tReportTimeExtendEntityList.get(0).getTxsj().getTime() <= (System.currentTimeMillis())){
                                    //查询机构下所有人员，调用蓝信接口提醒
                                    remind(entity.getJgbm(),entity.getBbkssj());
                                    //更新下一次提醒时间
                                    update(entity,tReportTimeExtendEntityList.get(0));
                                }
                            }
                        }else {
                            //如果未提醒过，先提醒，并把下一次提醒时间存入

                            //查询机构下所有人员，调用蓝信接口提醒
                            remind(entity.getJgbm(),entity.getBbkssj());
                            save(entity);
                        }
                    }
                }
            }
        }
    }

    /**
     * 保存下一次提醒时间
     * @param entity
     * @throws Exception
     */
    public void save(TReportTimeEntity entity) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        //获取报备开始时间
        String today = simpleDateFormat.format(new Date());
        String time = today + " "+ entity.getBbkssj() + ":00";
        long nextTime = dateFormat.parse(time).getTime();

        //获取提醒时间
        if ("1".equals(entity.getTxsj())){
            nextTime -= 1000 * 60 * 5;
        }else if ("2".equals(entity.getTxsj())){
            nextTime -= 1000 * 60 * 10;
        }else if ("3".equals(entity.getTxsj())){
            nextTime -= 1000 * 60 * 30;
        }else if ("4".equals(entity.getTxsj())){
            nextTime -= 1000 * 60 * 60;
        }

        //获取下一次提醒时间
        if ("0".equals(entity.getTxpc())){
            nextTime += 1000 * 60 * 5;
        }else if ("1".equals(entity.getTxpc())){
            nextTime += 1000 * 60 * 10;
        }else if ("2".equals(entity.getTxpc())){
            nextTime += 1000 * 60 * 30;
        }else if ("3".equals(entity.getTxpc())){
            nextTime += 1000 * 60 * 60;
        }

        Date txsj = new Date(nextTime);

        //保存下一次更新时间
        TReportTimeExtendEntity tReportTimeExtendEntity = new TReportTimeExtendEntity();
        tReportTimeExtendEntity.setReportTimeId(entity.getId());
        tReportTimeExtendEntity.setSfjs("0");
        tReportTimeExtendEntity.setTxsj(txsj);
        tReportTimeExtendMapper.insert(tReportTimeExtendEntity);
    }

    /**
     * 更新下一次提醒时间
     * @param entity
     * @param tReportTimeExtendEntity
     */
    public void update(TReportTimeEntity entity, TReportTimeExtendEntity tReportTimeExtendEntity) throws Exception {
        long nextTime = tReportTimeExtendEntity.getTxsj().getTime();
        if ("0".equals(entity.getTxpc())){
            nextTime += 1000 * 60 * 5;
        }else if ("1".equals(entity.getTxpc())){
            nextTime += 1000 * 60 * 10;
        }else if ("2".equals(entity.getTxpc())){
            nextTime += 1000 * 60 * 30;
        }else if ("3".equals(entity.getTxpc())){
            nextTime += 1000 * 60 * 60;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");;

        //获取报备结束时间
        String today = dateFormat.format(new Date());
        String time = today + " "+ entity.getBbkssj() + ":00";
        long bbkssj = format.parse(time).getTime();

        if (nextTime > bbkssj ){
            // 更新状态为结束
            tReportTimeExtendEntity.setSfjs("1");
            tReportTimeExtendMapper.updateById(tReportTimeExtendEntity);
        }else {
            //更新下一次时间
            tReportTimeExtendEntity.setTxsj(new Date(nextTime));
            tReportTimeExtendMapper.updateById(tReportTimeExtendEntity);
        }
    }



    /**
     * 判断是否需要提醒
     * @param entity
     * @return
     * @throws Exception
     */
    public Boolean isRemind(TReportTimeEntity entity) throws Exception {
        DateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String date = formatDay.format(new Date()) + " " + entity.getBbkssj() + ":00";
        Long kssj = simpleDateFormat.parse(date).getTime();

        if (kssj > System.currentTimeMillis() - 1000 * 60 * 4){ //用于验证如果新增的报备时间早于当前时间则不用提醒 减四分钟是因为当前时间等于报备开始时间，也可以进行提醒
            //开始时间-提醒时间 <= 当前时间 需要进行提醒
            if ("1".equals(entity.getTxsj())){
                if (kssj - 1000 * 60 * 5 < System.currentTimeMillis()){
                    return true;
                }
            }else if ("2".equals(entity.getTxsj())){
                if (kssj - 1000 * 60 * 10 < System.currentTimeMillis()){
                    return true;
                }
            }else if ("3".equals(entity.getTxsj())){
                if (kssj - 1000 * 60 * 30 < System.currentTimeMillis()){
                    return true;
                }
            }else if ("4".equals(entity.getTxsj())){
                if (kssj - 1000 * 60 * 60 < System.currentTimeMillis()){
                    return true;
                }
            }
        }
        return false;
    }


    public void remind(String jgbm, String time){
        HrQueryWrapper<TReportUserEntity> queryWrapper = new HrQueryWrapper<>();
        queryWrapper.eq("jgbm",jgbm);
        List<TReportUserEntity> list = tReportUserMapper.selectList(queryWrapper);

        List<Map<String, Object>> userList = new ArrayList<>();

        if (!ObjectUtils.isEmpty(list) && list.size() > 0){
            for (TReportUserEntity entity : list){
                Map<String, Object> lanxinParam = new HashMap<>();
                // 调用蓝信推送
                lanxinParam.put("phone", entity.getSjh());
                lanxinParam.put("title", "警力报备提醒");
                lanxinParam.put("content", "今日" + time + "需进行警力报备，请您按时进行报备！");
                userList.add(lanxinParam);
            }
            Map<String, Object> lanxinmap = new HashMap<>();
            lanxinmap.put("list", userList);
            LanXinThread.add(lanxinmap);
        }
    }


    /**
     * 删除已结束的提醒时间
     */
    @XxlJob("CancelReportTimeExtend")
    private void CancelReportTimeExtend(){
        HrQueryWrapper<TReportTimeExtendEntity> hrQueryWrapper = new HrQueryWrapper<>();
        hrQueryWrapper.eq("sfjs","1");
        tReportTimeExtendMapper.delete(hrQueryWrapper);
    }
}
