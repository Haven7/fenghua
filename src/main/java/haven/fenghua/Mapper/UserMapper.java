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
public interface UserMapper {


    /**
     * 查询测试
     */
    @Select("select * from user")
    List<Map<String, Object>> queryAll(Map<String, Object> params);





    /**
     * 查询情报记录
     */
    @Select({"<script>SELECT * from qbxx_qbgl_info\n" +
            "WHERE isdelete=0 \n" +
            "<if test=\"spzt != null and spzt != ''\">  and spzt in (${spzt})</if> \n" +
            "<if test=\"sslb != null and sslb != ''\">  and sslb=#{sslb}</if> \n" +
            "<if test=\"cjrjh != null and cjrjh != ''\">  and cjrjh=#{cjrjh}</if>\n" +
            "<if test=\"gjc != null and gjc != ''\">  and gjc like '%${gjc}%'and xxzw like '%${gjc}%'and bt like '%${gjc}%'</if>\n" +
            "<if test =\"staTime != null and staTime !='' \">and djsj &gt;= #{staTime} </if>\n" +
            "<if test =\"endTime != null and endTime !='' \">and djsj &lt;= #{endTime} </if>\n" +
            "ORDER BY  spzt DESC ,djsj DESC " +
            "</script>"})
    List<Map<String, Object>> queryQb(Map<String, Object> params);

    /**
     * 查询情报记录
     */
    @Select({"<script>SELECT * from qbxx_qbgl_info\n" +
            "WHERE qbxxid=#{qbxxid} \n" +
            "</script>"})
    Map<String, Object> queryQbByqbxxId(Map<String, Object> params);

    /**
     * 查询情报记录
     */
    @Select({"<script>SELECT * from qbxx_qbgl_info\n" +
            "WHERE id=#{id} \n" +
            "</script>"})
    Map<String, Object> queryQbById(Map<String, Object> params);

    /**
     * 查询情报附件记录
     */
    @Select({"<script>SELECT * from qbxx_qbgl_fj_info\n" +
            "WHERE qbxxid=#{qbxxid} \n" +
            "</script>"})
    List<Map<String, Object>> queryQbFj(Map<String, Object> params);


    /**
     * 情报登记
     */
    @Insert("INSERT INTO qbxx_qbgl_info(qbxxid,bt,gjc,xxzw,qbxslb,jjcd,fszt,xsly,qbxxlylb,asjbz,ssztlx,sslb,sjrsgm,sjssgm,ssyy,bxxs,sasj,saddlb,fsbw,safsdqh,fsdxz,sfmgsq,sjzdhd,sswp,qbxspj,csjx,wlmthdqk,cjrjh,cjrxm,cjrsfzh,cjrdwdm,cjrdwmc,djsj,spzt,isdelete,sfbcn,qxcy,tcy,mscy,szwx,rksj,qtgjc,fxdj,fxd,sfbb,jsdwdm,bssj,bgcx,sfzzbb,tbrxm,tbrsfzh,tbrdwdm,tbrdwmc,tbsj,jsdwmc)VALUES\n" +
            "(#{qbxxid},#{bt},#{gjc},#{xxzw},#{qbxslb},#{jjcd},#{fszt},#{xsly},#{qbxxlylb},#{asjbz},#{ssztlx},#{sslb},#{sjrsgm},#{sjssgm},#{ssyy},#{bxxs},#{sasj},#{saddlb},#{fsbw},#{safsdqh},#{fsdxz},#{sfmgsq},#{sjzdhd},#{sswp},#{qbxspj},#{csjx},#{wlmthdqk},#{cjrjh},#{cjrxm},#{cjrsfzh},#{cjrdwdm},#{cjrdwmc},#{djsj},#{spzt},#{isdelete},#{sfbcn},#{qxcy},#{tcy},#{mscy},#{szwx},#{rksj},#{qtgjc},#{fxdj},#{fxd},#{sfbb},#{jsdwdm},#{bssj},#{bgcx},#{sfzzbb},#{tbrxm},#{tbrsfzh},#{tbrdwdm},#{tbrdwmc},#{tbsj},#{jsdwmc})")
    @Options(keyColumn = "id", keyProperty = "id", useGeneratedKeys = true)
    int saveQbgl(Map<String, Object> params);


    /**
     * 情报上报
     */
    @Update("update qbxx_qbgl_info set sfbb=#{sfbb},jsdwdm=#{jsdwdm},bssj=#{bssj},bgcx=#{bgcx},sfzzbb=#{sfzzbb},spzt=#{spzt}," +
            "tbrxm=#{tbrxm},tbrsfzh=#{tbrsfzh},tbrdwdm=#{tbrdwdm},tbrdwmc=#{tbrdwmc},tbsj=#{tbsj} where qbxxid=#{qbxxid}")
    int pushQbgl(Map<String, Object> params);


    /**
     * 情报修改
     */
    @Update("update qbxx_qbgl_info set qbxxid=#{qbxxid},sfbb=#{sfbb},jjcd=#{jjcd},fszt=#{fszt},xsly=#{xsly},asjbz=#{asjbz},bt=#{bt},gjc=#{gjc},xxzw=#{xxzw},ssztlx=#{ssztlx},sslb=#{sslb},sjrsgm=#{sjrsgm},sjssgm=#{sjssgm},ssyy=#{ssyy}," +
            "bxxs=#{bxxs},sasj=#{sasj},saddlb=#{saddlb},fsbw=#{fsbw},safsdqh=#{safsdqh},fsdxz=#{fsdxz},sfmgsq=#{sfmgsq},sjzdhd=#{sjzdhd},sswp=#{sswp},csjx=#{csjx},wlmthdqk=#{wlmthdqk},cjrxm=#{cjrxm},cjrsfzh=#{cjrsfzh},cjrdwdm=#{cjrdwdm},cjrdwmc=#{cjrdwmc}," +
            "jsdwdm=#{jsdwdm},bssj=#{bssj},bgcx=#{bgcx},sfbcn=#{sfbcn},qbxxlylb=#{qbxxlylb},sfzzbb=#{sfzzbb},qxcy=#{qxcy},tcy=#{tcy},mscy=#{mscy},qbxspj=#{qbxspj},rksj=#{rksj},zjxgsj=#{zjxgsj}," +
            "qtgjc=#{qtgjc},xgrjh=#{xgrjh},xgrxm=#{xgrxm},xgrsfzh=#{xgrsfzh},xgrdwdm=#{xgrdwdm},xgrdwmc=#{xgrdwmc} " +
            "WHERE qbxxid = #{qbxxid}")
    int updateQbgl(Map<String, Object> params);

    /**
     * 情报修改审批状态
     */
    @Update("update qbxx_qbgl_info set spzt=#{spzt} " +
            "WHERE id = #{id}")
    int updateQbglZt(Map<String, Object> params);

    /**
     * 情报删除
     */
    @Update("UPDATE qbxx_qbgl_info SET xgrjh=#{xgrjh},xgrxm=#{xgrxm},xgrsfzh=#{xgrsfzh},xgrdwdm=#{xgrdwdm},xgrdwmc=#{xgrdwmc},isdelete = #{isdelete},spzt = #{spzt},spzt = #{spzt},szwx = #{szwx},zjxgsj = #{zjxgsj} WHERE qbxxid = #{qbxxid}\n")
    int deleteQbgl(Map<String, Object> params);


    /**
     * 添加附件
     */
    @Insert("INSERT INTO qbxx_qbgl_fj_info(qbxxid,fj,sfsc)\n" +
            "VALUES(#{qbxxid},#{fj},#{sfsc})")
    @Options(keyColumn = "id", keyProperty = "id", useGeneratedKeys = true)
    int saveFj(Map<String, Object> params);

    /**
     * 删除附件
     */
    @Delete("DELETE FROM qbxx_qbgl_fj_info WHERE qbxxid=#{qbxxid}")
    int deleteFj(Map<String, Object> params);


}
