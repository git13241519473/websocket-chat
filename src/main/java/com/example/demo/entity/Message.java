package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Message implements Serializable {
    private static final long serialVersionUID = -3297516187418539828L;

    private String type; //0:系统消息 1:用户消息
    private String userId; //消息发起人
    private String toUserId; //消息接收人
    private String contentText; //消息内容
    private Date createDate; //消息生成时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getCreateDate() {
        return createDate;
    }


}
