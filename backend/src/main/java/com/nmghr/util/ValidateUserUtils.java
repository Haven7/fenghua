/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.nmghr.util;

import com.nmghr.common.Result;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.net.URLDecoder;
import java.util.Map;

/**
 * 验证请求参数
 *
 * @author zhangcg.
 * @date 2019/11/28 - 12:00 下午.
 */
@Component
public class ValidateUserUtils {

    public static final int ERROR_NUMBER = 999996;
    public static final String ERROR_NUMBER2 = "999996";
    public static final String ERROR_MESSAGE = "请求参数不能为空";
    public static final String NOT_NULL_MESSAGE_DEVICE_ID = "deviceId不能为空";
    public static final String NOT_NULL_MESSAGE_USER_ID = "userId不能为空";
    public static final String NOT_NULL_MESSAGE_JH = "警号不能为空";
    public static final String REMOTE_ERROR_MESSAGE = "message";
    public static final String HEADER_ERROR_MESSAGE = "请求头信息异常";
    public static final String NOT_NULL_MESSAGE_DEPART_CODE = "按照公安部要求，此项查询需要用户所在单位编码";
    public static final String NOT_NULL_MESSAGE_DEPART_NAME = "按照公安部要求，此项查询需要用户所在单位名称";
    public static final String NOT_NULL_MESSAGE_USER_ID_CARD = "按照公安部要求，此项查询需要用户身份证号";
    public static final String NOT_NULL_MESSAGE_USER_REAL_NAME = "按照公安部要求，此项查询需要用户真实姓名";
    public static final String NOT_NULL_MESSAGE_USER_CERTIFICATE_NO = "按照公安部要求，此项查询需要数字证书编号";
    public static final String NOT_AUTH_MESSAGE = "未查到用户的授权信息";

    /**
     * 验证参数
     *
     * @param requestParam
     * @return
     */
    @SneakyThrows
    public static Result validateParam(Map<String, Object> requestParam) {
//        if (ObjectUtils.isEmpty(requestParam.get("x-device-sn"))) {
//            return Result.error(ERROR_NUMBER, NOT_NULL_MESSAGE_DEVICE_ID);
//        } else {
//            requestParam.put("deviceId", requestParam.get("x-device-sn"));
//        }
//
//        if (ObjectUtils.isEmpty(requestParam.get("x-user-name"))) {
//            return Result.error(ERROR_NUMBER, NOT_NULL_MESSAGE_JH);
//        } else {
//            requestParam.put("jh", requestParam.get("x-user-name"));
//        }
//        if (ObjectUtils.isEmpty(requestParam.get("certificateno"))) {
//            return Result.error(ERROR_NUMBER, NOT_NULL_MESSAGE_USER_CERTIFICATE_NO);
//        } else {
//            requestParam.put("certificateNo", requestParam.get("certificateno"));
//        }
//        if (ObjectUtils.isEmpty(requestParam.get("x-user-sfzh"))) {
//            return Result.error(ERROR_NUMBER, NOT_NULL_MESSAGE_USER_ID_CARD);
//        } else {
//            requestParam.put("idCard", requestParam.get("x-user-sfzh"));
//        }
//        if (ObjectUtils.isEmpty(requestParam.get("x-user-real-name"))) {
//            return Result.error(ERROR_NUMBER, NOT_NULL_MESSAGE_USER_REAL_NAME);
//        } else {
//            requestParam.put("realName", URLDecoder.decode(requestParam.get("x-user-real-name").toString(), "UTF-8"));
//        }
//        if (ObjectUtils.isEmpty(requestParam.get("x-depart-code"))) {
//            return Result.error(ERROR_NUMBER, NOT_NULL_MESSAGE_DEPART_CODE);
//        } else {
//            requestParam.put("departCode", requestParam.get("x-depart-code"));
//        }
//        if (ObjectUtils.isEmpty(requestParam.get("x-depart-name"))) {
//            return Result.error(ERROR_NUMBER, NOT_NULL_MESSAGE_DEPART_NAME);
//        } else {
//            requestParam.put("departName", requestParam.get("x-depart-name"));
//        }

        return Result.ok(requestParam);
    }
}
