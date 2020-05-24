package com.example.demo.websocket;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.Message;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class MyEncode implements Encoder.Text<Message> {
    @Override
    public String encode(Message message) throws EncodeException {
        return JSONObject.toJSONString(message);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}
