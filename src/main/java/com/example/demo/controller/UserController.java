package com.example.demo.controller;

//import org.springframework.amqp.rabbit.core.RabbitTemplate;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
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

    @Autowired
    private UserService userService;

    public static void main(String[] args) {
        System.out.println("hello world!");
    }

    /*@Autowired
    RabbitTemplate rabbitTemplate;  //使用RabbitTemplate,这提供了接收/发送等等方法

    @GetMapping("/sendMessage")
    @ResponseBody
    public String sendMessage(){
        User user = new User();
        user.setId("15344556677");
        user.setEmail("15344556677@qq.com");
        user.setRealname("自然资源部");
        user.setSex("1");
        user.setTelPhone("15344556677");
        user.setUsername("admin-zrzyb");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", user);
        jsonObject.put("changeType", "1");
        rabbitTemplate.convertAndSend("topicExchange", "sync.user.add", jsonObject.toJSONString());
        return "ok!";
    }*/

    //创建房间
    public static Map<String, HashSet<User>> room = new HashMap<>();

    @GetMapping("createRoom")
    @ResponseBody
    public Map createRoom(String userIds){
        if(StringUtils.isNotBlank(userIds)){
            String ids[] = StringUtils.split(userIds, ",");
            HashSet<User> set = new HashSet<>();
            for(String id: ids){
                set.add(userService.getUserById(id));
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
