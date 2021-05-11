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
 * @date 2021/2/3 - 9:38.
 */
package com.nmghr.config;

import com.nmghr.db.config.CodeGenerator;

public class TestConfig {

    public static void main(String[] args) {
        //模块目录名
        String moduleName = "backend";
        //包名
        String packageName = "com.nmghr";
        //数据库连接
        String sqlUrl = "jdbc:mysql://192.168.42.207:3306/hr-police-report?useUnicode=true&useSSL=false&characterEncoding=utf8";
        //数据库驱动
        String driverName = "com.mysql.jdbc.Driver";
        //数据库用户名
        String userName = "root";
        //数据库密码
        String password = "123456";
        //要成代码的表名
//        String[] tableName = {"tb_user", "tb_user_extend"};
        String[] tableName = {"t_statistics_time"};
        String author = "haven";
        CodeGenerator.run(packageName, moduleName, sqlUrl, driverName, userName, password, tableName, author);
    }

}
