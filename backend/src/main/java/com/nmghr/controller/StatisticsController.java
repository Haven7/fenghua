package com.nmghr.controller;

import com.nmghr.advice.SecretAnnotation;
import com.nmghr.common.Result;
import com.nmghr.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author HavenF
 */
@RestController
@RequestMapping(value = "/statistics")
public class StatisticsController {
    @Autowired
    private StatisticsService statisticsService;

    /**
     * 首页统计数
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/homeStatistics",method = RequestMethod.POST)
    @ResponseBody
    @SecretAnnotation(decode=true,encode=true)
    public Result homeStatistics(@RequestBody Map<String, Object> params){
        if (ObjectUtils.isEmpty(params.get("jgbm"))){
            return Result.error("登录人部门编码不能为空");
        }
        return statisticsService.homeStatistics(params);
    }

    /**
     * 实时统计
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/realTimeStatistics",method = RequestMethod.POST)
    @ResponseBody
    @SecretAnnotation(decode=true,encode=true)
    public Result realTimeStatistics(@RequestBody Map<String, Object> params){
        if (ObjectUtils.isEmpty(params.get("jgbm"))){
            return Result.error("登录人部门编码不能为空");
        }
        return statisticsService.realTimeStatistics(params);
    }


      /**
     * 所有部门查询
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/queryAllDept",method = RequestMethod.POST)
    @ResponseBody
    @SecretAnnotation(decode=true,encode=true)
    public Result queryAllDept(@RequestBody Map<String, Object> params){
        return Result.ok(statisticsService.queryAllDept(params));
    }


      /**
     * 子部门查询
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/queryChildDept",method = RequestMethod.POST)
    @ResponseBody
    @SecretAnnotation(decode=true,encode=true)
    public Result queryChildDept(@RequestBody Map<String, Object> params){
        if(ObjectUtils.isEmpty(params.get("jgbm"))){
            return Result.error("请输入机构编码");
        }
        return Result.ok(statisticsService.queryChildDept(params));
    }




}
