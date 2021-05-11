/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.nmghr.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <Description/>
 *
 * @author haven
 */
@Mapper
public interface StatisticsMapper {


    /**
     * 统计在岗人数（管理员）
     */
    @Select({"<script>SELECT count(1) FROM `t_report_user` WHERE zgzt=1 " +
            "<if test=\"jgbm != null and jgbm != ''\">  and  jgbm in (${jgbm}) </if></script> "})
    int queryWorkingPer(Map<String, Object> params);

    /**
     * 统计在岗车数（管理员）
     */
    @Select({"<script>SELECT count(1) FROM `t_report_car` WHERE zgzt=1 " +
            "<if test=\"jgbm != null and jgbm != ''\">  and  jgbm in (${jgbm}) </if></script> "})
    int queryWorkingCar(Map<String, Object> params);



    /**
     * 统计报备人次、车次（管理员）
     */
    @Select({"<script>SELECT count(1) FROM `t_report_record` WHERE bbzt=2 " +
            "<if test=\"jgbm != null and jgbm != ''\">  and  jgbm in (${jgbm}) </if>" +
            "<if test=\"tjkssj != null and tjkssj != ''\">  and  bbkssj  &gt;= #{tjkssj} </if>" +
            "<if test=\"tjjssj != null and tjjssj != ''\">  and  bbkssj  &lt;= #{tjjssj} </if>" +
            "<if test=\"bbfl != null and bbfl != ''\">  and  bbfl =#{bbfl} </if></script> "})
    int statisticsReport(Map<String, Object> params);


    /**
     * 统计报备人次、车次（管理员）
     */
    @DS("db1")
    @Select({"<script>SELECT * FROM `t_sys_dept` WHERE dept_code=#{jgbm}</script> "})
    Map<String, Object> queryDepart(Map<String, Object> params);

  /**
     * 查询机构统计时间（管理员）
     */
    @Select({"<script>SELECT * FROM t_statistics_time WHERE 1=1 group by jgbm </script> "})
    List<Map<String, Object>> queryJgList();


    /**
     * 保存定时统计结果（管理员）
     */
    @Insert("INSERT INTO t_statistics(tjkssj,tjjssj,cjsj,xgsj,rs,cs,jgmc,jgbm\n)VALUES\n" +
            "(#{tjkssj},#{tjjssj},#{cjsj},#{xgsj},#{rs},#{cs},#{jgmc},#{jgbm})")
    @Options(keyColumn = "id", keyProperty = "id", useGeneratedKeys = true)
    int saveStatistics(Map<String, Object> params);



}
