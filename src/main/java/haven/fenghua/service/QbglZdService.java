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

import haven.fenghua.api.Result;
import haven.fenghua.mapper.QbglZdMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class QbglZdService {

    @Autowired
    private QbglZdMapper qbglZdMapper;

    /**
     * 查询部门
     */
    public List<Map<String, Object>> queryDepart(Map<String, Object> params) {
        params.put("app_id",1004);
        return qbglZdMapper.queryQbZd(params);
    }

}
