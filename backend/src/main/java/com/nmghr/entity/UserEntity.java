/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/**
 * <Description/>
 *
 * @author zhangjun.
 * @date 2021/4/16 - 11:27.
 */
package com.nmghr.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_user")
public class UserEntity {


    @TableId(type = IdType.INPUT)
    private Integer id;
    /**
     * 手机号
     */
    private String sjh;
    /**
     * 身份证号
     */
    private String sfzh;
    /**
     * 警号
     */
    private String jh;
    /**
     * 机构名称
     */
    private String jgmc;
    /**
     * 机构编码
     */
    private String jgbm; /**
     * 姓名
     */
    private String xm; /**
     * 性别
     */
    private String xb; /**
     * 出生日期
     */
    private String csrq; /**
     * 审批状态
     */
    private String spzt;

    /**
     * 添加时间
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date tjsj;
    /**
     * 审批时间
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date spsj;
    /**
     * 注册成功时间
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date zccgsj;
    /**
     * 审批人姓名
     */
    private String sprxm;
    /**
     * 审批人手机号
     */
    private String sprsjh;
    /**
     * 审批人警号
     */
    private String sprjh;
    /**
     * 修改时间
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date xgsj;
    /**
     * 创建时间
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date cjsj;

}
