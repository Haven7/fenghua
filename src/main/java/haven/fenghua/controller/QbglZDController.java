/**
 *  * Created by fhw on 2020/7/27
 *  * <p/>
 *  * Copyright (c) 2015-2015
 *  * Apache License
 *  * Version 2.0, January 2004
 *  * http://www.apache.org/licenses/
 *  
 */
package haven.fenghua.controller;

import haven.fenghua.api.Result;
import haven.fenghua.service.QbglZdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/Car", method = RequestMethod.POST)
public class QbglZDController {


//    @Autowired
//    private QbglZdService qbglZdService;


//    /**
//     * 查询情报字典
//     */
//    @RequestMapping(value = "/qbzd", method = RequestMethod.POST)
//    public Result queryQbZd(@RequestHeader Map<String, Object> requestHeader, @RequestBody Map<String, Object> params) {
//        //验证是否有用户信息
//        qbglZdService.queryDepart(params);
//        return Result.ok();
//    }

    /**
     * 查询情报字典
     */
    @RequestMapping(value = "/hello", method = RequestMethod.POST)
    public Result hello( @RequestBody Map<String, Object> params) {


        return Result.ok("访问成功");
    }

}
