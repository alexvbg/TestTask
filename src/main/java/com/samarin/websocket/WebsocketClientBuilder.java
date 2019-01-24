package com.samarin.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.concurrent.ExecutionException;

@Slf4j
public class WebsocketClientBuilder {

    private static String URL = "wss://iqoption.com/echo/websocket";


    public static WebSocketStompClient init() throws ExecutionException, InterruptedException {
        WebSocketClient websocketClient = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(websocketClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        StompSessionHandler sessionHandler = new AuthSessionClientHandler();
        ListenableFuture<StompSession> connect = stompClient.connect(URL, sessionHandler);
        StompSession stompSession = connect.get();
        stompSession.isConnected();
        return stompClient;
    }


}
