package com.nmghr.controller;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nmghr.advice.SecretAnnotation;
import com.nmghr.common.Result;
import com.nmghr.service.UserService;
import com.nmghr.util.ValidateUserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HavenF
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 查询授权信息
     *
     * @param requestParam
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryApproval", method = RequestMethod.POST)
    @SecretAnnotation(decode=true,encode=true)
    public Result queryApproval(@RequestBody Map<String, Object> requestParam) {
        if(ObjectUtils.isEmpty(requestParam.get("sjh"))){
            return Result.error("请输入手机号");
        }else {
            requestParam.put("telNumber",requestParam.get("sjh"));
        }
        return userService.queryAuthority(requestParam);
    }


    /**
     * 用户注册
     * @param requestParam 参数列表
     * @return 返回提示信息
     */
    @RequestMapping(value = "/register",method = RequestMethod.POST)
    @ResponseBody
    @SecretAnnotation(decode=true,encode=true)
    public Result register(@RequestHeader Map<String, Object> requestHeader, @RequestBody Map<String,Object> requestParam){
        //todo 入参验证
        //验证是否有用户信息
        Result valid = ValidateUserUtils.validateParam(requestHeader);
        if (!valid.isSuccess()) {
            return valid;
        }
        //        Map<String, Object> userInfo = (Map<String, Object>) valid.getResult();
        return userService.register(requestParam);
    }



    /**
     * 用户注册修改
     * @param requestParam 参数列表
     * @return 返回提示信息
     */
    @RequestMapping(value = "/updateRegister",method = RequestMethod.POST)
    @ResponseBody
    @SecretAnnotation(decode=true,encode=true)
    public Result updateRegister(@RequestHeader Map<String, Object> requestHeader, @RequestBody Map<String,Object> requestParam){
        //todo 入参验证
        //验证是否有用户信息
        Result valid = ValidateUserUtils.validateParam(requestHeader);
        if (!valid.isSuccess()) {
            return valid;
        }
        //        Map<String, Object> userInfo = (Map<String, Object>) valid.getResult();
        Map<String,Object> result = userService.updateRegister(requestParam);
        if(ObjectUtils.isEmpty(result)){
            return Result.error("修改失败");
        }
        return Result.ok(result);
    }



    /**
     * 查询注册信息(用户)
     * @param requestParam 参数列表
     * @return 返回提示信息
     */
    @RequestMapping(value = "/queryRegister",method = RequestMethod.POST)
    @ResponseBody
    @SecretAnnotation(decode=true,encode=true)
    public Result queryRegister(@RequestHeader Map<String, Object> requestHeader, @RequestBody Map<String,Object> requestParam){
        //todo 入参验证
        //验证是否有用户信息
//        Result valid = ValidateUserUtils.validateParam(requestHeader);
//        if (!valid.isSuccess()) {
//            return valid;
//        }
        //        Map<String, Object> userInfo = (Map<String, Object>) valid.getResult();
        if(ObjectUtils.isEmpty(requestParam.get("sjh"))){
            return Result.error("请输入手机号");
        }
        Map<String,Object> result = userService.queryRegister(requestParam);

        return Result.ok(result);
    }



    /**
     * 查询注册信息(管理员)
     * @param requestParam 参数列表
     * @return 返回提示信息
     */
    @RequestMapping(value = "/queryUser",method = RequestMethod.POST)
    @ResponseBody
    @SecretAnnotation(decode=true,encode=true)
    public Result queryUser(@RequestParam(value = "pageNum", defaultValue = "0") int pageNum,
                            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                            @RequestHeader Map<String, Object> requestHeader, @RequestBody Map<String,Object> requestParam){
        //todo 入参验证
        //验证是否有用户信息
//        Result valid = ValidateUserUtils.validateParam(requestHeader);
//        if (!valid.isSuccess()) {
//            return valid;
//        }
        //        Map<String, Object> userInfo = (Map<String, Object>) valid.getResult();
        if(!ObjectUtils.isEmpty(requestParam.get("pageNum"))&&!ObjectUtils.isEmpty(requestParam.get("pageSize"))){
//            return  Result.error("请输入分页参数");
            pageNum = Integer.parseInt(requestParam.get("pageNum").toString());
            pageSize =  Integer.parseInt(requestParam.get("pageSize").toString());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Map<String, Object>> result = userService.queryUser(requestParam);
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(result);
        return Result.ok(pageInfo);
    }


    /**
     * 审批注册信息(管理员)
     * @param requestParam 参数列表
     * @return 返回提示信息
     */
    @RequestMapping(value = "/approveUser",method = RequestMethod.POST)
    @ResponseBody
    @SecretAnnotation(decode=true,encode=true)
    public Result approveUser(@RequestHeader Map<String, Object> requestHeader, @RequestBody Map<String,Object> requestParam){
        //todo 入参验证
        //验证是否有用户信息
        Result valid = ValidateUserUtils.validateParam(requestHeader);
        if (!valid.isSuccess()) {
            return valid;
        }
//                Map<String, Object> userInfo = (Map<String, Object>) valid.getResult();
        return userService.approveUser(requestParam);
    }
}
