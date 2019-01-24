package com.samarin.websocket;

import com.samarin.websocket.model.AuthMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;

@Slf4j
public class AuthSessionClientHandler extends StompSessionHandlerAdapter {

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        log.info("afterConnected");
//        session.send("/echo/websocket/", "{name:“ssid”, “msg”: “8ba860cb9ea0e799820972fa02df0721“}");
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        log.info("getPayloadType");
        return AuthMessage.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        log.info("handleFrame");

        super.handleFrame(headers, payload);
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        log.error("handleException", exception);
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        log.error("handleTransportError", exception);
    }

}

