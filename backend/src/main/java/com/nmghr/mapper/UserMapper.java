package com.nmghr.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * @author HavenF
 */
@Mapper
public interface UserMapper {

    /**
     * 注册登记
     */
    @Insert("INSERT INTO t_user(sjh,sfzh,jh,jgmc,jgbm,xm,xb,csrq,spzt,tjsj,xgsj,cjsj,zccgsj,jylx)VALUES\n" +
            "(#{sjh},#{sfzh},#{jh},#{jgmc},#{jgbm},#{xm},#{xb},#{csrq},#{spzt},#{tjsj},#{xgsj},#{cjsj},#{zccgsj},#{jylx})")
    @Options(keyColumn = "id", keyProperty = "id", useGeneratedKeys = true)
    int register(Map<String, Object> params);


    /**
     * 注册修改
     */
    @Update("<script>update t_user set sjh=#{sjh},sfzh=#{sfzh},jh=#{jh},jgmc=#{jgmc},jgbm=#{jgbm},xm=#{xm},xb=#{xb},csrq=#{csrq},xgsj=#{xgsj},spzt=#{spzt},jylx=#{jylx} WHERE sjh = #{sjh}</script>")
    int updateRegister(Map<String, Object> params);

    /**
     * 查询注册信息(用户)
     */
    @Select({"<script>SELECT * FROM `t_user` WHERE 1=1  " +
            "<if test=\"id != null and id != ''\">  and id =  #{id} </if> " +
            "<if test=\"spzt != null and spzt != ''\">  and  spzt in (${spzt}) </if> " +
            "<if test=\"sfzh != null and sfzh != ''\">  and sfzh =  #{sfzh} </if> " +
            "<if test=\"sjh != null and sjh != ''\">  and sjh =  #{sjh} </if> " +
            "<if test=\"xm != null and xm != ''\">  and xm= #{xm} </if></script> "})
   Map<String, Object> queryRegister(Map<String, Object> params);


    /**
     * 查询注册信息（管理员）
     */
    @Select({"<script>SELECT * FROM `t_user` WHERE 1=1  " +
            "<if test=\"jgbm != null and jgbm != ''\">  and  jgbm in (${jgbm}) </if>" +
            "<if test=\"spzt != null and spzt != ''\">  and  spzt in (${spzt}) </if> " +
            "<if test=\"sjh != null and sjh != ''\">  and  sjh =  #{sjh} </if> " +
            "<if test=\"sfzh != null and sfzh != ''\">  and sfzh =  #{sfzh} </if> " +
            "<if test=\"xm != null and xm != ''\">  and xm= #{xm} </if></script> "})
    List<Map<String, Object>> queryUser(Map<String, Object> params);

    /**
     * 审批注册信息（管理员）
     */
    @Update("update t_user set spzt=#{spzt},spsj=#{spsj},sprxm=#{sprxm},sprsjh=#{sprsjh},sprjh=#{sprjh},spnr=#{spnr},xgsj=#{xgsj}" +
            " WHERE sjh = #{sjh}")
    int approveUser(Map<String, Object> params);

}
