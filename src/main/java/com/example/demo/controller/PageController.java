package com.example.demo.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

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

}
