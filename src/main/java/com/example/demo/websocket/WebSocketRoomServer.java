package com.example.demo.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.MessageRoom;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 一对一聊天
 */
@ServerEndpoint(value = "/imserverroom/{roomId}/{userId}")
@Component
public class WebSocketRoomServer {
    public static Logger log = LoggerFactory.getLogger(WebSocketRoomServer.class);

    //静态变量，用来记录当前在线连接数。
    public static Map<String, AtomicInteger> onlineMap = new ConcurrentHashMap<>();

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static Map<String, CopyOnWriteArraySet<WebSocketRoomServer>> webSocketMap = new ConcurrentHashMap<>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //接收userId
    private String userId = "";
    //接收roomId
    private String roomId = "";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("roomId") String roomId, @PathParam("userId") String userId) {
        this.session = session;
        /*String[] split = StringUtils.split(id, "-");
        this.roomId = split[0];
        this.userId = split[1];*/
        this.roomId = roomId;
        this.userId = userId;

        CopyOnWriteArraySet<WebSocketRoomServer> friends = webSocketMap.get(roomId);
        if (friends == null) {
            synchronized (webSocketMap) {
                if (!webSocketMap.containsKey(roomId)) {
                    friends = new CopyOnWriteArraySet<>();
                    webSocketMap.put(roomId, friends);
                }
            }
        }
        friends.add(this); //有一个人进入房间
        //房间在线人数加 1
        addOnlineCount(roomId);

        UserService userService = new UserService();
        User user = userService.getUserById(userId);
        log.info("用户:" + user.getName() + "，进入房间："+ roomId +"， 当前房间在线人数为:" + getOnlineCount(roomId));

        //发送消息提示
        try {
            MessageRoom msgObj = createMsg("0", "webSocket", this.roomId, "欢迎【"+ user.getName() +"】进入房间");
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
        if (webSocketMap.containsKey(roomId)) {
            CopyOnWriteArraySet<WebSocketRoomServer> friends = webSocketMap.get(roomId);
            //从set中删除
            if(friends != null){
                friends.remove(this);
            }
            subOnlineCount(roomId);
        }
        log.info("用户:" + userId + "退出房间, 当前在线人数为:" + getOnlineCount(roomId));
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
                String roomId = jsonObject.getString("roomId");
                //传送给对应toUserId用户的websocket
                if (StringUtils.isNotBlank(roomId) && webSocketMap.containsKey(roomId)) {
                    MessageRoom msgObj = createMsg("1", this.userId, roomId, jsonObject.getString("contentText"));
                    sendMessage(msgObj);
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
        log.error("未知错误--> userId: " + this.userId + "，roomId: "+ this.roomId +"， 原因:" + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(MessageRoom message) throws IOException {
        String roomId = message.getRoomId();
        CopyOnWriteArraySet<WebSocketRoomServer> roomServers = webSocketMap.get(roomId);
        if(roomServers != null){
            try {
                for(WebSocketRoomServer server: roomServers){
                    server.session.getBasicRemote().sendText(JSONObject.toJSONString(message));
                }
            }catch (Exception e){
                log.error("发送消息异常。。。{}", message);
                e.printStackTrace();
            }
        }
    }


    public MessageRoom createMsg(String type, String userId, String roomId, String contentText){
        MessageRoom msgObj = new MessageRoom();
        msgObj.setType(type);
        msgObj.setUserId(userId);
        msgObj.setRoomId(roomId);
        msgObj.setContentText(contentText);
        msgObj.setCreateDate(new Date());
        return msgObj;
    }

    public static synchronized int getOnlineCount(String roomId) {
        if(!onlineMap.containsKey(roomId)){
            onlineMap.put(roomId, new AtomicInteger(0));
        }
        AtomicInteger onLineCount = onlineMap.get(roomId);
        return onLineCount.get();
    }

    //当前在线人数加1
    public static synchronized void addOnlineCount(String roomId) {
        if(!onlineMap.containsKey(roomId)){
            onlineMap.put(roomId, new AtomicInteger(0));
        }
        AtomicInteger onLineCount = onlineMap.get(roomId);
        onLineCount.getAndIncrement();
    }

    //当前在线人数减1
    public static synchronized void subOnlineCount(String roomId) {
        AtomicInteger onLineCount = onlineMap.get(roomId);
        onLineCount.getAndDecrement();
    }
}