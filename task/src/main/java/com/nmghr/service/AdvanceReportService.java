/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/**
 * <Description/>
 *
 * @author zhangjun.
 * @date 2021/4/26 - 11:37.
 */
package com.nmghr.service;

import com.nmghr.db.wrapper.HrQueryWrapper;
import com.nmghr.entity.TReportCarEntity;
import com.nmghr.entity.TReportRecordEntity;
import com.nmghr.entity.TReportUserEntity;
import com.nmghr.mapper.TReportCarMapper;
import com.nmghr.mapper.TReportRecordMapper;
import com.nmghr.mapper.TReportUserMapper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;

@Component
public class AdvanceReportService {

    @Autowired
    private TReportCarMapper tReportCarMapper;

    @Autowired
    private TReportUserMapper tReportUserMapper;

    @Autowired
    private TReportRecordMapper tReportRecordMapper;

    @XxlJob("ReportStart")
    private void ReportStart(){
        System.out.println("预报备开始报备定时任务开始！！---->1");

        // 查询所有未开始的预报备信息
        HrQueryWrapper<TReportRecordEntity> queryWrapper = new HrQueryWrapper<>();
        queryWrapper.eq("bblx","1").eq("bbzt","0");
        queryWrapper.apply("bbkssj <= now()");
        List<TReportRecordEntity> list = tReportRecordMapper.selectList(queryWrapper);
        Date date = new Date();

        //循环处理
        if (!ObjectUtils.isEmpty(list) && list.size() > 0){
            for (TReportRecordEntity entity : list){
                //判断是报备人还是报备车 0-人 1-车
                if ("0".equals(entity.getBbfl())){

                    // 查询是否有未结束的实时报备信息
                    HrQueryWrapper<TReportRecordEntity> recordQueryWrapper = new HrQueryWrapper<>();
                    recordQueryWrapper.eq("bblx","0").eq("bbzt","1").eq("bbrjh",entity.getBbrjh());
                    List<TReportRecordEntity> recordList = tReportRecordMapper.selectList(recordQueryWrapper);

                    if (!ObjectUtils.isEmpty(recordList) && recordList.size() > 0){
                        //修改报备记录
                        TReportRecordEntity tReportRecordEntity = new TReportRecordEntity();
                        tReportRecordEntity.setBbzt("2");
                        tReportRecordEntity.setId(entity.getId());
                        tReportRecordMapper.updateById(tReportRecordEntity);

                    }else {
                        HrQueryWrapper<TReportUserEntity> userQueryWrapper = new HrQueryWrapper<>();
                        userQueryWrapper.eq("jh",entity.getBbrjh());

                        //修改人员在岗状态
                        TReportUserEntity tReportUserEntity = new TReportUserEntity();
                        tReportUserEntity.setJwd(entity.getJwd());
                        tReportUserEntity.setBbzt("1");
                        // 0-休息，1-执勤
                        if ("0".equals(entity.getBbqk())){
                            tReportUserEntity.setZgzt("2");
                        }else {
                            tReportUserEntity.setZgzt("1");
                        }
                        tReportUserEntity.setXgsj(date);
                        tReportUserMapper.update(tReportUserEntity,userQueryWrapper);

                        //修改报备记录
                        TReportRecordEntity tReportRecordEntity = new TReportRecordEntity();
                        tReportRecordEntity.setBbzt("1");
                        tReportRecordEntity.setId(entity.getId());
                        tReportRecordMapper.updateById(tReportRecordEntity);
                    }
                }else {
                    // 查询是否有未结束的实时报备信息
                    HrQueryWrapper<TReportRecordEntity> recordQueryWrapper = new HrQueryWrapper<>();
                    recordQueryWrapper.eq("bblx","0").eq("bbzt","1").eq("bbcph",entity.getBbcph());
                    List<TReportRecordEntity> recordList = tReportRecordMapper.selectList(recordQueryWrapper);

                    if (!ObjectUtils.isEmpty(recordList) && recordList.size() > 0){
                        //修改报备记录
                        TReportRecordEntity tReportRecordEntity = new TReportRecordEntity();
                        tReportRecordEntity.setBbzt("2");
                        tReportRecordEntity.setId(entity.getId());
                        tReportRecordMapper.updateById(tReportRecordEntity);
                    }else {
                        HrQueryWrapper<TReportCarEntity> carQueryWrapper = new HrQueryWrapper<>();
                        carQueryWrapper.eq("cph",entity.getBbcph());

                        //修改车在岗状态
                        TReportCarEntity tReportCarEntity = new TReportCarEntity();
                        tReportCarEntity.setJwd(entity.getJwd());
                        tReportCarEntity.setBbzt("1");
                        // 0-休息，1-执勤
                        if ("0".equals(entity.getBbqk())){
                            tReportCarEntity.setZgzt("2");
                        }else {
                            tReportCarEntity.setZgzt("1");
                        }
                        tReportCarEntity.setXgsj(date);
                        tReportCarMapper.update(tReportCarEntity,carQueryWrapper);

                        //修改报备记录
                        TReportRecordEntity tReportRecordEntity = new TReportRecordEntity();
                        tReportRecordEntity.setBbzt("1");
                        tReportRecordEntity.setId(entity.getId());
                        tReportRecordMapper.updateById(tReportRecordEntity);
                    }
                }



            }
        }
    }


    @XxlJob("ReportEnd")
    private void ReportEnd(){
        System.out.println("预报备结束报备定时任务开始！！---->1");

        // 查询所有已开始的预报备信息
        HrQueryWrapper<TReportRecordEntity> queryWrapper = new HrQueryWrapper<>();
        queryWrapper.eq("bblx","1").eq("bbzt","1");
        queryWrapper.apply("bbjssj <= now()");
        List<TReportRecordEntity> list = tReportRecordMapper.selectList(queryWrapper);
        Date date = new Date();

        //循环处理
        if (!ObjectUtils.isEmpty(list) && list.size() > 0){
            for (TReportRecordEntity entity : list){
                //判断是报备人还是报备车 0-人 1-车
                if ("0".equals(entity.getBbfl())){
                    HrQueryWrapper<TReportUserEntity> userQueryWrapper = new HrQueryWrapper<>();
                    userQueryWrapper.eq("jh",entity.getBbrjh());

                    //修改人员在岗状态
                    TReportUserEntity tReportUserEntity = new TReportUserEntity();
                    tReportUserEntity.setJwd(entity.getJwd());
                    tReportUserEntity.setBbzt("0");
                    tReportUserEntity.setZgzt("0");
                    tReportUserEntity.setXgsj(date);
                    tReportUserMapper.update(tReportUserEntity,userQueryWrapper);
                }else {
                    HrQueryWrapper<TReportCarEntity> carQueryWrapper = new HrQueryWrapper<>();
                    carQueryWrapper.eq("cph",entity.getBbcph());

                    //修改车在岗状态
                    TReportCarEntity tReportCarEntity = new TReportCarEntity();
                    tReportCarEntity.setJwd(entity.getJwd());
                    tReportCarEntity.setBbzt("0");
                    tReportCarEntity.setZgzt("0");
                    tReportCarEntity.setXgsj(date);
                    tReportCarMapper.update(tReportCarEntity,carQueryWrapper);
                }


                //修改报备记录
                TReportRecordEntity tReportRecordEntity = new TReportRecordEntity();
                tReportRecordEntity.setBbzt("2");
                tReportRecordEntity.setId(entity.getId());
                tReportRecordMapper.updateById(tReportRecordEntity);
            }
        }
    }
}
