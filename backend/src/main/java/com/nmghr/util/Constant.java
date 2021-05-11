/**
 *  * Created by zj on 2020/8/3
 *  * <p/>
 *  * Copyright (c) 2015-2015
 *  * Apache License
 *  * Version 2.0, January 2004
 *  * http://www.apache.org/licenses/
 *  
 */
package com.nmghr.util;

public class Constant {

    //Upms 服务器地址

//        public static final String UPMS_SERVER_IP_ADDRESS = "http://192.168.42.197:8081";
//    public static final String UPMS_SERVER_IP_ADDRESS = "http://192.168.52.149:8089";
//    public static final String UPMS_SERVER_IP_ADDRESS = "http://26.3.4.31:8081";
    public static final String UPMS_SERVER_IP_ADDRESS = "http://26.3.2.178:8090";

    //地图
    public static final String Map_SERVER_IP_ADDRESS = "http://26.3.12.44:8086/geooper/api/rgeo";

    //查询所有下级部门
    public static final String QUERY_DEPARTMENT_URL = "/deptOut/queryAllByNo";

      //查询下一级部门
    public static final String QUERY_CHILD_DEPARTMENT_URL = "/deptOut/queryDeptByParNo";

    //查询所有部门树
    public static final String QUERY_DEPARTMENTTREE_URL = "/tSysDept/queryTreeList";

    //登陆授权
    public static final String QUERY_LANXINUSER_URL = "/login/lanxinUserLogin";

    //注册upms信息
    public static final String SAVE_UPMSUSER_URL = "/tSysUser/saveUserInfo";



}
