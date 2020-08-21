/**
 *  * Created by fhw on 2020/7/27
 *  * <p/>
 *  * Copyright (c) 2015-2015
 *  * Apache License
 *  * Version 2.0, January 2004
 *  * http://www.apache.org/licenses/
 *  
 */
package haven.fenghua.Mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface CustomerMapper {
    /**
     * 查询顾客列表
     */
    @Select({"<script>select * from user_info\n" +
            "WHERE isdelete=0 \n" +
            "<if test=\"customer_name != null and customer_name != ''\">  and customer_name like '%${customer_name}%'</if> \n" +
            "<if test=\"customer_phone != null and customer_phone != ''\">  and customer_phone=#{customer_phone}</if> \n" +
            "<if test=\"car_number != null and car_number != ''\">  and car_number like '%${car_number}%'</if>\n" +
            "<if test=\"car_type != null and car_type ''\">  and car_type like '%${car_type}%'</if>\n" +
            "ORDER BY id DESC" +
            "</script>"})
    List<Map<String, Object>> queryAll(Map<String, Object> params);

}
