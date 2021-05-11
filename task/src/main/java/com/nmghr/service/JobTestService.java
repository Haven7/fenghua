/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.nmghr.service;

import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

/**
 * <Description/>
 *
 * @author guowq.
 * @Date 2021/1/28 - 9:44.
 */
@Component
public class JobTestService {

    @XxlJob("StartJob")
    private void StartJob(){
        System.out.println("自建定时任务开始了！！---->1");
    }

}
