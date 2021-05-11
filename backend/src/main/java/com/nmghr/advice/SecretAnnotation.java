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
 * @date 2021/4/16 - 11:34.
 */
package com.nmghr.advice;

import java.lang.annotation.*;


/**
 * 自定义注解
 * 加解密
 *
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SecretAnnotation {

    /**
     * 是否加密
     * 默认false
     * 加密时传值为true
     * @return
     */

    boolean encode() default false;
    /**
     * 是否解密
     * 默认为false，
     * 解密时传值为true
     * @return
     */
    boolean decode() default false;
}
