/**
 *  * Created by fhw on 2020/7/27
 *  * <p/>
 *  * Copyright (c) 2015-2015
 *  * Apache License
 *  * Version 2.0, January 2004
 *  * http://www.apache.org/licenses/
 *  
 */
package haven.fenghua.service;

import haven.fenghua.Mapper.CustomerMapper;
import haven.fenghua.Mapper.UserMapper;
import haven.fenghua.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CustomerService {

    @Autowired
    private CustomerMapper customerMapper;


    /**
     * 查询顾客列表
     */
    public Result queryAll(Map<String, Object> params) {
        List<Map<String, Object>> map = customerMapper.queryAll(params);
        return Result.ok(map);
    }

}
