package com.lzl.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@ServerEndpoint("/ws/{sid}")
public class WebSocketserver {

    private static Map<String, Session> sessionMap = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        log.info("建立连接：{}", sid);
        sessionMap.put(sid, session);
    }

    @OnMessage
    public void onMessage(Session session, String message, @PathParam("sid") String sid) {
        log.info("接收到消息：{}", message);
    }

    @OnClose
    public void onClose(Session session, @PathParam("sid") String sid) {
        log.info("连接关闭：{}", sid);
        sessionMap.remove(sid);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.info("出现了异常");
        throwable.printStackTrace();
    }

    public void sendMessage(String message) throws Exception {
        log.info("发送消息");
        Collection<Session> values = sessionMap.values();
        if (!CollectionUtils.isEmpty(values)) {
            for (Session session : values) {
                session.getBasicRemote().sendText(message);
            }
        }
    }
}
