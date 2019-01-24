package com.samarin.websocket;

import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;

public class MySimpleStompFrameHandler implements StompFrameHandler {

    public Type getPayloadType(StompHeaders stompHeaders) {
        System.out.println("Headers " + stompHeaders.toString());
        return String.class;
    }

    public void handleFrame(StompHeaders stompHeaders, Object o) {
        System.out.println("Msg " + o.toString());
    }
}