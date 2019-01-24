package com.samarin.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
public class TestHandler implements WebSocketHandler {

    int i = 0;

    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("afterConnectionEstablished");
    }

    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        log.info("handleMessage" + message.getPayload().toString());
        String textMessage = "{\"name\":\"ssid\",\"msg\":\"6259810b3881d635594a281883b4f51d\"}";
        TextMessage message1 = new TextMessage(textMessage.getBytes());
        if (i == 0) {
            session.sendMessage(message1);
            log.info("send msg - " + textMessage);
            i++;
        }
    }

    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("handleTransportError", exception);
    }

    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("afterConnectionClosed");
    }

    public boolean supportsPartialMessages() {
        return false;
    }
}
