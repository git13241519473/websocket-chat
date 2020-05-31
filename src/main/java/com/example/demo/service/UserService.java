package com.example.demo.service;

import com.example.demo.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public User getUserById(String id){
        User user = new User();
        user.setId(id);
        String name = "";
        if(StringUtils.equals(id, "hanxin")){
            name = "韩信";
        }else if(StringUtils.equals(id, "laohu")){
            name = "老虎";
        }else if(StringUtils.equals(id, "nake")){
            name = "娜可";
        }else if(StringUtils.equals(id, "xuance")){
            name = "玄策";
        }else if(StringUtils.equals(id, "houzi")){
            name = "猴子";
        }
        user.setName(name);
        return user;
    }

}
