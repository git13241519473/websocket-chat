package com.example.demo.controller;

import com.example.demo.entity.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashSet;

@RestController
@RequestMapping("/page")
public class PageController {

    /**
     * 简单跳转页面，不带其他参数
     * @param pageName
     * @return ModelAndView
     */
    @RequestMapping("/to/{pageName}")
    public ModelAndView toPage(@PathVariable(value = "pageName") String pageName){
        ModelAndView mav = new ModelAndView();
        mav.setViewName(pageName);
        return mav;
    }
    //跳转聊天页面： http://localhost:8080/page/to/chat

    /**
     * 跳转到首页页面
     * @return
     */
    @GetMapping("index")
    public ModelAndView toIndexPage(){
        ModelAndView mav = new ModelAndView();
        mav.setViewName("fontpage/index");
        return mav;
    }

    /**
     * 跳转到群聊页面
     * @return
     */
    @GetMapping("roomChat")
    public ModelAndView toRoomChatPage(String roomId){
        ModelAndView mav = new ModelAndView();
        mav.addObject("roomId", roomId);
        HashSet<User> users = UserController.room.get(roomId);
        mav.addObject("users", users);
        mav.setViewName("roomChat");
        return mav;
    }
}
