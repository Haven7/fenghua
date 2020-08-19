/**
 *  * Created by fhw on 2020/7/27
 *  * <p/>
 *  * Copyright (c) 2015-2015
 *  * Apache License
 *  * Version 2.0, January 2004
 *  * http://www.apache.org/licenses/
 *  
 */
package haven.fenghua.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface QbglZdMapper {

    /**
     * 查询字典
     */
    @Select({"<script>SELECT * from dict_info\n" +
            "WHERE enable=1 \n" +
            "<if test=\"type != null and type != ''\">  and type=#{type}</if> \n" +
            "<if test=\"strcode != null and strcode != ''\">  and `code` LIKE '${strcode}%'</if> \n" +
            "<if test=\"endcode != null and endcode != ''\">  and `code` LIKE '%${endcode}'</if> \n" +
            "<if test=\"noendcode != null and noendcode != ''\">  and `code`  NOT LIKE  '%${noendcode}'</if> \n" +
            "</script>"})
    List<Map<String, Object>> queryQbZd(Map<String, Object> params);

    /**
     * 查询字典byid
     */
    @Select({"<script>SELECT name from dict_info\n" +
            "WHERE enable=1 \n" +
            "<if test=\"code != null and code != ''\">  and code=#{code}</if> \n" +
            "<if test=\"type != null and type != ''\">  and type=#{type}</if> \n" +
            "</script>"})
    String queryQbZdById(@Param("code") String code, @Param("type") String type);

}
