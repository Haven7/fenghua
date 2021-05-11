package com.nmghr.lanxin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nmghr.util.OkHttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author dotu
 */
public class LanXinThread extends Thread {

  private static final Logger log = LoggerFactory.getLogger(LanXinThread.class);
//  private BlueMsgNoticeService bluemsgNoticeService;
  private String reqUrl;
  private String grantType;
  private String appid;
  private String secret;
  private String linkmsg;

  private String TOKEN = "";
  private int TIMER = 0;

  private static ArrayBlockingQueue<Map<String, Object>> queue = new ArrayBlockingQueue<Map<String, Object>>(2000);

  public LanXinThread(String reqUrl, String grantType, String appid,
      String secret, String linkmsg) {
//    this.bluemsgNoticeService = bluemsgNoticeService;
    this.reqUrl = reqUrl;
    this.grantType = grantType;
    this.appid = appid;
    this.secret = secret;
    this.linkmsg = linkmsg;
  }

  public static void add(Map<String, Object> msg) {
    log.info("蓝信增加queque" + JSON.toJSONString(msg));
    queue.offer(msg);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void run() {
    while (true) {
      try {
        if (TIMER == 0) {
          getToken();
        }
        Thread.sleep(1000);
        Map<String, Object> taskMap = queue.poll();
        if (taskMap != null) {
          List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
          if (taskMap.containsKey("list")) {
            list = (List<Map<String, Object>>) taskMap.get("list");
          } else {
            list.add(taskMap);
          }
          for (Map<String, Object> task : list) {
            String phone = "";
            String title = "";
            String description = "";
            String iconLink = "";
            String fromName = "";
            String fromIconLink = "";
            String paramId = "";
            String msgType = "";

            if (task.containsKey("phone") && !StringUtils.isEmpty(task.get("phone"))) {
              phone = String.valueOf(task.get("phone"));
            }
            if (task.containsKey("title") && !StringUtils.isEmpty(task.get("title"))) {
              title = String.valueOf(task.get("title"));
            }
            if (task.containsKey("content") && !StringUtils.isEmpty(task.get("content"))) {
              description = String.valueOf(task.get("content"));
            }
            if (task.containsKey("iconLink") && !StringUtils.isEmpty(task.get("iconLink"))) {
              iconLink = String.valueOf(task.get("iconLink"));
            }
            if (task.containsKey("fromName") && !StringUtils.isEmpty(task.get("fromName"))) {
              fromName = String.valueOf(task.get("fromName"));
            }
            if (task.containsKey("fromIconLink") && !StringUtils.isEmpty(task.get("fromIconLink"))) {
              fromIconLink = String.valueOf(task.get("fromIconLink"));
            }
            if (task.containsKey("id") && !StringUtils.isEmpty(task.get("id"))) {
              paramId = String.valueOf(task.get("id"));
            }
            if (task.containsKey("msgType") && !StringUtils.isEmpty(task.get("msgType"))) {
              msgType = String.valueOf(task.get("msgType"));
            }
            // 手机号必须， 发送消息标题必须
            if (!"".equals(phone) && !"".equals(title)) {
              List<String> userIdList = new ArrayList<String>();
              // 获取蓝信用户ID
              String userId = userId(String.valueOf(task.get("phone")));
              if (!"".equals(userId)) {
                userIdList.add(userId);
              }
              // 发送消息
              notice(userIdList, title, description, iconLink, fromName, fromIconLink, paramId, msgType);
            }
          }
        }
        TIMER++;
        if (TIMER > 3000) {
          getToken();
        }
      } catch (Exception e) {
        log.error("error", e);
      }
    }
  }

  private void getToken() throws IOException {
    log.info("调用【获取Token】方法");
    String returnString = OkHttpUtil.getStringFromServer(
        reqUrl + "/v1/apptoken/create?grant_type=" + grantType + "&appid=" + appid + "&secret=" + secret);
    JSONObject json = (JSONObject) JSON.parse(returnString);
    String errCode = json.getString("errCode");
    log.info("获取token返回" + JSON.toJSONString(json));
    if ("0".equals(errCode)) {
      JSONObject data = json.getJSONObject("data");
      TOKEN = data.getString("app_token");
      TIMER = 1;
    }
  }

  private String userId(String phone) throws IOException {
    log.info("调用【通过唯一表示获取用户ID】方法{}", phone);
    log.info("TOKEN : {}", TOKEN);
    log.info("TIMER : {}", TIMER);
    if (!"".equals(TOKEN)) {
      String orgid = appid.split("-")[0];
      String returnString = OkHttpUtil.getStringFromServer(reqUrl + "/v2/staffs/id_mapping/fetch?app_token=" + TOKEN
          + "&org_id=" + orgid + "&id_type=mobile&id_value=" + phone);
      JSONObject json = (JSONObject) JSON.parse(returnString);
      String errCode = json.getString("errCode");
      log.info("获取用户ID返回" + JSON.toJSONString(json));
      if ("0".equals(errCode)) {
        JSONObject data = json.getJSONObject("data");
        return data.getString("staffId");
      } else {
        log.info("[mobile=" + phone + "]未获取到用户ID");
        return "";
      }
    }
    return "";
  }

  private void notice(List<String> userIdList, String title, String description, String iconLink, String fromName,
      String fromIconLink, String paramId, String msgType) throws IOException {
    log.info("调用【发送应用消息】方法{}", JSON.toJSONString(userIdList));
    log.info("TOKEN : {}", TOKEN);
    log.info("TIMER : {}", TIMER);
    if (!"".equals(TOKEN)) {
      JSONObject joOjbect = new JSONObject();
      joOjbect.put("userIdList", userIdList);
      if ("".equals(msgType)) {
        joOjbect.put("msgType", "text");
        // 发送文本信息
        String msgData = "{\"text\":{\"content\":\"" + description
            + "\", \"reminder\":{\"all\": false, \"userIds\": []}}}";
        joOjbect.put("msgData", JSONObject.parseObject(msgData));
      } else if ("linkCard".equals(msgType)) {
        joOjbect.put("msgType", "linkCard");
        // 发送连接信息
        String msgData = "{\"linkCard\":{\"title\":\"" + title + "\", \"description\":\"" + description
            + "\", \"iconLink\":\"" + iconLink + "\", \"link\":\"" + linkmsg + "?id=" + paramId + "\",\"fromName\":\""
            + fromName + "\",\"fromIconLink\":\"" + fromIconLink + "\"}}";
        joOjbect.put("msgData", JSONObject.parseObject(msgData));
      }
      String objectString = joOjbect.toJSONString();
      log.info("推送的信息: " + objectString);
      String value = OkHttpUtil.post(reqUrl + "/v1/messages/create?app_token=" + TOKEN, objectString);
      log.info("蓝信+返回: " + value);
    }
  }

  public static void main(String[] args) {
    try {
      String reqUrl = "https://apigw-cloud.lanxin.cn";
      String grantType = "client_credential";
      String appid = "10904320-9961472";
      String secret = "DC00A4B28AD648A2BE0A4119E51AF356";
      String returnString = OkHttpUtil.getStringFromServer(
          reqUrl + "/v1/apptoken/create?grant_type=" + grantType + "&appid=" + appid + "&secret=" + secret);
      JSONObject json = (JSONObject) JSON.parse(returnString);
      String errCode = json.getString("errCode");
      System.out.println("获取token返回" + JSON.toJSONString(json));
      String token = "";
      if ("0".equals(errCode)) {
        JSONObject data = json.getJSONObject("data");
        token = data.getString("app_token");
      }

      String orgid = appid.split("-")[0];
      String mobile = "15829380127";
      returnString = OkHttpUtil.getStringFromServer(reqUrl + "/v2/staffs/id_mapping/fetch?app_token=" + token
          + "&org_id=" + orgid + "&id_type=mobile&id_value=" + mobile);
      json = (JSONObject) JSON.parse(returnString);
      errCode = json.getString("errCode");
      System.out.println("获取user返回" + JSON.toJSONString(json));
      String userId = "";
      if ("0".equals(errCode)) {
        JSONObject data = json.getJSONObject("data");
        userId = data.getString("staffId");
      }

      List<String> userIdList = new ArrayList<>();
      userIdList.add(userId);

      JSONObject joOjbect = new JSONObject();
      joOjbect.put("userIdList", userIdList);
      // 发送文本信息
      String title = "测试消息发送";
      String description = "这是一张link卡片的描述，我写长一点，感觉长的样子会更加的好看一些。这么长够不够呀，哈哈哈哈哈哈。这是一张link卡片的描述，我写长一点，感觉长的样子会更加的好看一些。这么长够不够呀，哈哈哈哈哈哈";
      String iconLink = "";
      String linkmsg = "http://192.168.42.229:91/#/login";
      String fromName = "";
      String fromIconLink = "";
      String msgType = "";
      String paramId = "text";
      if ("".equals(msgType)) {
        joOjbect.put("msgType", "text");
        // 发送文本信息
        String msgData = "{\"text\":{\"content\":\"" + description
            + "\", \"reminder\":{\"all\": false, \"userIds\": []}}}";
        joOjbect.put("msgData", JSONObject.parseObject(msgData));
      } else if ("linkCard".equals(msgType)) {
        joOjbect.put("msgType", "linkCard");
        // 发送连接信息
        String msgData = "{\"linkCard\":{\"title\":\"" + title + "\", \"description\":\"" + description
            + "\", \"iconLink\":\"" + iconLink + "\", \"link\":\"" + linkmsg + "?id=" + paramId + "\",\"fromName\":\""
            + fromName + "\",\"fromIconLink\":\"" + fromIconLink + "\"}}";
        joOjbect.put("msgData", JSONObject.parseObject(msgData));
      }
      String objectString = joOjbect.toJSONString();
      log.info("推送的信息: " + objectString);
      String value = OkHttpUtil.post(reqUrl + "/v1/messages/create?app_token=" + token, objectString);
      log.info("蓝信+返回: " + value);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
