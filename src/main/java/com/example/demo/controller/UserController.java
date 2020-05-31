package com.example.demo.controller;

//import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/user")
public class UserController {

    public static void main(String[] args) {
        System.out.println("hello world!");
    }

    /*@Autowired
    RabbitTemplate rabbitTemplate;  //使用RabbitTemplate,这提供了接收/发送等等方法

    @GetMapping("/sendMessage")
    @ResponseBody
    public String sendMessage(){
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "message: M A N ";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String, Object> manMap = new HashMap<>();
        manMap.put("messageId", messageId);
        manMap.put("messageData", messageData);
        manMap.put("createTime", createTime);
        rabbitTemplate.convertAndSend("topicExchange", "sync.user.add", manMap);
        return "ok!";
    }*/

    //创建房间
    public static Map<String, HashSet<String>> room = new HashMap<>();

    @GetMapping("createRoom")
    @ResponseBody
    public Map createRoom(String userIds){
        if(StringUtils.isNotBlank(userIds)){
            String ids[] = StringUtils.split(userIds, ",");
            HashSet<String> set = new HashSet<>();
            for(String id: ids){
                set.add(id);
            }
            String roomId = "room" + new Date().getTime();
            room.put(roomId, set);
            Map<String, Object> result = new HashMap<>();
            result.put("roomId", roomId);
            result.put("userIds", set);
            return result;
        }
        return null;
    }
}
