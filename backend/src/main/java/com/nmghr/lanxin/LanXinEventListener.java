package com.nmghr.lanxin;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * @author dotu
 */
@Component
public class LanXinEventListener implements ApplicationListener<ApplicationReadyEvent>, Ordered {

//  @Autowired
//  protected BlueMsgNoticeService bluemsgNoticeService;

  @Override
  public int getOrder() {
    return 100;
  }

  public void onApplicationEvent(ApplicationReadyEvent arg0) {
    new LanXinThread( GlobalConfig.reqUrl, GlobalConfig.grantType,
        GlobalConfig.appid, GlobalConfig.secret, GlobalConfig.linkmsg).start();
  }

}
