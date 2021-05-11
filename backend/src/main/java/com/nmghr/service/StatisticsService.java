/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.nmghr.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nmghr.common.Result;
import com.nmghr.mapper.StatisticsMapper;
import com.nmghr.util.Constant;
import com.nmghr.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <Description/>
 *
 * @author haven.
 */
@Slf4j
@Service
public class StatisticsService {

    @Autowired
    StatisticsMapper statisticsMapper;


    //首页统计
    public Result homeStatistics(Map<String, Object> params) {
        params.put("deptCode",params.get("jgbm"));
        Map<String, Object> resultMap = new HashMap<>();
        //管理员查看
        //查询所属部门
        List jgMap = null;
        if (!ObjectUtils.isEmpty(jgMap)) {
            String jg = ",";
            StringBuilder sb = new StringBuilder();
            sb.append(params.get("deptCode").toString());
//            sb.append(jg);
            for (int i = 0; i < jgMap.size(); i++) {
                sb.append(jg);
                sb.append(jgMap.get(i));
            }
            params.put("jgbm",sb.toString());
            //有下级部门
            int carNum =  statisticsMapper.queryWorkingCar(params);
            int perNum =  statisticsMapper.queryWorkingPer(params);
            resultMap.put("carNum",carNum);
            resultMap.put("perNum",perNum);
        } else {
           //无下级部门
            int carNum =  statisticsMapper.queryWorkingCar(params);
            int perNum =  statisticsMapper.queryWorkingPer(params);
            resultMap.put("carNum",carNum);
            resultMap.put("perNum",perNum);
        }
        return Result.ok(resultMap);
    }




    /**
     * 查询所有部门
     * @param
     * @return
     */
    public List queryAllDept(Map<String,Object> params){
        String url = Constant.UPMS_SERVER_IP_ADDRESS + Constant.QUERY_DEPARTMENTTREE_URL;

        if(!ObjectUtils.isEmpty(params.get("jgbm"))){
//            String queryParas = ;
            url=url+"?deptCode="+params.get("jgbm").toString();
        }
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "*/*");
        headers.put("Cache-Control", "no-cache");
        headers.put("Content-Type", "application/json; charset=UTF-8");
        String post = "";
        List list = new ArrayList();
        try {
            post = HttpUtil.post(url,null,"",headers);
            JSONObject jsonObject = JSON.parseObject(post);
            list = JSONArray.parseObject(jsonObject.getString("data"), List.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }




    /**
     * 查询下属部门
     * @param
     * @return
     */
    public List queryChildDeptTree(Map<String,Object> params){
        String url = Constant.UPMS_SERVER_IP_ADDRESS + Constant.QUERY_CHILD_DEPARTMENT_URL;
        Map<String,String> dataMap = new HashMap<>();
        dataMap.put("deptCode",params.get("jgbm").toString());
        String data = JSONObject.toJSONString(dataMap);
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "*/*");
        headers.put("Cache-Control", "no-cache");
        headers.put("Content-Type", "application/json; charset=UTF-8");
        String post = "";
        List list = new ArrayList();
        try {
            post = HttpUtil.post(url,data,headers);
            JSONObject jsonObject = JSON.parseObject(post);
            list = JSONArray.parseObject(jsonObject.getString("data"), List.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }



    /**
     * 查询子部门
     * @param
     * @return
     */
    public List queryChildDept(Map<String,Object> params){
        String url = Constant.UPMS_SERVER_IP_ADDRESS + Constant.QUERY_CHILD_DEPARTMENT_URL;
        Map<String,String> dataMap = new HashMap<>();
        dataMap.put("deptCode",params.get("jgbm").toString());
        String data = JSONObject.toJSONString(dataMap);
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "*/*");
        headers.put("Cache-Control", "no-cache");
        headers.put("Content-Type", "application/json; charset=UTF-8");
        String post = "";
        List list = new ArrayList();
        try {
            post = HttpUtil.post(url,data,headers);
            JSONObject jsonObject = JSON.parseObject(post);
            list = JSONArray.parseObject(jsonObject.getString("data"), List.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


}
