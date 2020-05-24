package com.example.demo.websocket;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.Message;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;


/**
 * @author zhengkai.blog.csdn.net
 */
@ServerEndpoint(value = "/imserver/{userId}", encoders = {MyEncode.class})
@Component
public class WebSocketServer {
    public static Logger log = LoggerFactory.getLogger(WebSocketServer.class);

    //静态变量，用来记录当前在线连接数。
    public static AtomicInteger onlineCount = new AtomicInteger(0);

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //接收userId
    private String userId = "";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId = userId;
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
            webSocketMap.put(userId, this); //加入map中
        } else {
            webSocketMap.put(userId, this); //加入map中
            addOnlineCount();   //在线数加1
        }
        log.info("用户连接:" + userId + ", 当前在线人数为:" + getOnlineCount());
        //把未读过的消息进行推送 TODO
        //发送消息提示
        try {
            Message msgObj = createMsg("0", "webSocket", this.userId, "连接成功");
            sendMessage(msgObj);
        } catch (IOException e) {
            log.error("用户:" + userId + ",网络异常!!!!!!");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
            //从set中删除
            subOnlineCount();
        }
        log.info("用户退出:" + userId + ",当前在线人数为:" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("用户消息:" + userId + ",报文:" + message);
        //可以群发消息
        //消息保存到数据库、redis
        if (StringUtils.isNotBlank(message)) {
            try {
                //解析发送的报文
                JSONObject jsonObject = JSON.parseObject(message);
                //追加发送人(防止串改)
                jsonObject.put("fromUserId", this.userId);
                String toUserId = jsonObject.getString("toUserId");
                //传送给对应toUserId用户的websocket
                if (StringUtils.isNotBlank(toUserId) && webSocketMap.containsKey(toUserId)) {
                    Message msgObj = createMsg("1", this.userId, toUserId, jsonObject.getString("contentText"));
                    webSocketMap.get(toUserId).sendMessage(msgObj);
                } else {
                    log.error("请求的userId:" + toUserId + "不在该服务器上");
                    //如果不在这个服务器上，发送到mysql或者redis
                    //TODO 可以把要发送的消息存储到数据库中
                }
                //TODO 保存message对象到数据库
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户错误:" + this.userId + ",原因:" + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(Message message) throws IOException {
        try {
            this.session.getBasicRemote().sendObject(message);
        } catch (EncodeException e) {
            log.error("发送消息异常。。。{}", message);
            e.printStackTrace();
        }
    }


    public Message createMsg(String type, String userId, String toUserId, String contentText){
        Message msgObj = new Message();
        msgObj.setType(type);
        msgObj.setUserId(userId);
        msgObj.setToUserId(toUserId);
        msgObj.setContentText(contentText);
        msgObj.setCreateDate(new Date());
        return msgObj;
    }

    public static synchronized int getOnlineCount() {
        return onlineCount.get();
    }

    //当前在线人数加1
    public static synchronized void addOnlineCount() {
        onlineCount.getAndIncrement();
    }

    //当前在线人数减1
    public static synchronized void subOnlineCount() {
        onlineCount.getAndDecrement();
    }
}