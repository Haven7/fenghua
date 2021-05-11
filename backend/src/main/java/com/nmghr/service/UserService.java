package com.nmghr.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nmghr.common.Result;
import com.nmghr.mapper.StatisticsMapper;
import com.nmghr.mapper.UserMapper;
import com.nmghr.util.Constant;
import com.nmghr.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HavenF
 */
@Slf4j
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StatisticsMapper statisticsMapper;


    /**
     * 查询授权信息
     *
     * @param params
     * @return
     */
    public Result queryAuthority(Map<String, Object> params) {
        String url = Constant.UPMS_SERVER_IP_ADDRESS + Constant.QUERY_LANXINUSER_URL;
        Map<String,String> map = new HashMap<>();
        map.put("telNumber",params.get("telNumber").toString());
//        map.put("userName",params.get("userName").toString());
        String post = null;
        /* 定义请求头 */
        String data = JSONObject.toJSONString(map);
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "*/*");
        headers.put("Cache-Control", "no-cache");
        headers.put("Content-Type", "application/json; charset=UTF-8");
        /* 发起请求并处理返回结果 */
        try {
            post = HttpUtil.post(url, data, headers);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("查询用户授权信息，得到响应为：{}", post);
        JSONObject jsonObject = JSON.parseObject(post);
        if (!ObjectUtils.isEmpty(jsonObject) && !ObjectUtils.isEmpty(jsonObject.get("code"))) {
            if ("000000".equals(jsonObject.get("code").toString())) {
                Map<String, String> resultObject = (Map<String, String>) jsonObject.get("data");
                return Result.ok(resultObject);
            }
        }


        return Result.error("查询服务异常");
    }


    /**
     * 保存注册信息
     *
     * @param requestMap
     * @return
     */
    @Transactional
    public Result register(Map<String, Object> requestMap) {
        Map<String, Object> params = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String now = sdf.format(new Date());
        if (!ObjectUtils.isEmpty(requestMap.get("sjh"))) {
            params.put("sjh", requestMap.get("sjh"));
        }else {
            return Result.error("请输入手机号");
        }
        params.put("spzt", "0,1");
        List<Map<String, Object>> resultMap = userMapper.queryUser(params);
        int count = 0;
        if (resultMap.size() > 0) {
            requestMap.put("xgsj", now);
            requestMap.put("spzt", "0");
            count = userMapper.updateRegister(requestMap);
            if (count > 0) {
                return Result.ok("注册成功");
            }
        } else {
            requestMap.put("tjsj", now);
            requestMap.put("cjsj", now);
            requestMap.put("xgsj", now);
            requestMap.put("zccgsj", now);
            requestMap.put("spzt", "0");
            count = userMapper.register(requestMap);
            if (count > 0) {
                return Result.ok("注册成功");
            }
        }
        return null;
    }


    /**
     * 修改注册信息
     *
     * @param requestMap
     * @return
     */
    @Transactional
    public Map<String, Object> updateRegister(Map<String, Object> requestMap) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        int count = 0;
        String now = sdf.format(new Date());
        requestMap.put("xgsj", now);
        requestMap.put("spzt", "0");
        count = userMapper.updateRegister(requestMap);
        if (count == 0) {
            return null;
        }
        return requestMap;
    }

    /**
     * 查询注册信息(用户)
     *
     * @param requestMap
     * @return
     */
    @Transactional
    public Map<String, Object> queryRegister(Map<String, Object> requestMap) {
        Map<String, Object> resultMap = userMapper.queryRegister(requestMap);
        return resultMap;
    }


    /**
     * 查询注册信息(管理员)
     *
     * @param requestMap
     * @return
     */
    @Transactional
    public List<Map<String, Object>> queryUser(Map<String, Object> requestMap) {
        if (ObjectUtils.isEmpty(requestMap.get("spzt"))) {
            return null;
        }
        if (ObjectUtils.isEmpty(requestMap.get("jgbm"))) {
            return null;
        }
        requestMap.put("deptCode",requestMap.get("jgbm"));
        List jgMap = null;
        String jg = ",";
        StringBuilder sb = new StringBuilder();
        sb.append(requestMap.get("deptCode").toString());
////        sb.append(jg);
        for (int i = 0; i < jgMap.size(); i++) {
                sb.append(jg);
                sb.append(jgMap.get(i));
        }
        requestMap.put("jgbm",sb.toString());
        List<Map<String, Object>> resultMap = userMapper.queryUser(requestMap);
        return resultMap;
    }

    /**
     * 审批注册信息(管理员)
     *
     * @param requestMap
     * @return
     */
    public Result approveUser(Map<String, Object> requestMap) {
        if (ObjectUtils.isEmpty(requestMap.get("spzt"))) {
            return Result.error("请选择审批选项");
        }
        if (ObjectUtils.isEmpty(requestMap.get("sprxm"))) {
            return Result.error("请输入审批人信息");
        }
        if (ObjectUtils.isEmpty(requestMap.get("sprsjh"))) {
            return Result.error("请输入审批人信息");
        }
        if (ObjectUtils.isEmpty(requestMap.get("sprjh"))) {
            return Result.error("请输入审批人信息");
        }
        if (ObjectUtils.isEmpty(requestMap.get("spnr"))) {
            requestMap.put("spnr","");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String now = sdf.format(new Date());
        requestMap.put("spsj", now);
        requestMap.put("xgsj", now);
        Integer count = userMapper.approveUser(requestMap);
        if (count > 0) {
            if("2".equals(requestMap.get("spzt"))){
                Map<String, Object> saveNewUpms = new HashMap<>();
                saveNewUpms.put("sjh",requestMap.get("sjh"));
                Result result =  saveNewUpms(saveNewUpms);
                if(!result.isSuccess()){
                    Result.error("注册失败");
                }
            }
            return Result.ok("审批成功");
        }
        return Result.error("审批失败");
    }




    /**
     * 注册警员upms信息
     *
     * @param params
     * @return
     */
    public Result saveNewUpms(Map<String, Object> params) {
        String url = Constant.UPMS_SERVER_IP_ADDRESS + Constant.SAVE_UPMSUSER_URL;
        Map<String, Object> userMap = userMapper.queryRegister(params);
        if(ObjectUtils.isEmpty(userMap)){
            return Result.error("未查询到用户信息");
        }
        Map<String,String> dataMap = new HashMap<>();
        dataMap.put("realName",userMap.get("xm").toString());
        dataMap.put("telNumber",userMap.get("sjh").toString());
        dataMap.put("userName",userMap.get("jh").toString());
        dataMap.put("userIdCard",userMap.get("sfzh").toString());

        Map<String, Object>  jgInfo =  statisticsMapper.queryDepart(userMap);
        if(ObjectUtils.isEmpty(jgInfo)){
            return Result.error("暂无该机构数据");
        }
        dataMap.put("deptIds",jgInfo.get("id").toString());
//        dataMap.put("passWord","51110a187abd25044036f83f0d88e7a9");
        dataMap.put("passWord","888888");
        dataMap.put("enabled","1");
        dataMap.put("deleteable","0");
        dataMap.put("isUpms","1");
        dataMap.put("userType",userMap.get("jylx").toString());
        log.info("注册upms用户信息，参数为：{}", dataMap);
        String post = null;
        /* 定义请求头 */
        String data = JSONObject.toJSONString(dataMap);
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "*/*");
        headers.put("Cache-Control", "no-cache");
        headers.put("Content-Type", "application/json; charset=UTF-8");
        /* 发起请求并处理返回结果 */
        try {
            post = HttpUtil.post(url, data, headers);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("注册upms用户信息，得到响应为：{}", post);
        JSONObject jsonObject = JSON.parseObject(post);
        if (!ObjectUtils.isEmpty(jsonObject) && !ObjectUtils.isEmpty(jsonObject.get("code"))) {
            if ("000000".equals(jsonObject.get("code").toString())) {
//                Map<String, String> resultObject = (Map<String, String>) jsonObject.get("data");
                return Result.ok("注册成功");
            }
        }
        return Result.error("注册服务异常");
    }
}
