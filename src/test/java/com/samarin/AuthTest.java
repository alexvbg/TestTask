package com.samarin;


import com.samarin.websocket.TestHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


@Slf4j
public class AuthTest {

    private static String URL = "wss://iqoption.com/echo/websocket";

    @BeforeClass
    public void beforeTest() {
    }

    @Test
    public void test() throws InterruptedException, ExecutionException, IOException, URISyntaxException {

        StandardWebSocketClient websocketClient = new StandardWebSocketClient();
        Map<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("name", "ssid");
        stringObjectHashMap.put("msg", "119f01df39e5a1e06da3e4b8d138b506");
        websocketClient.setUserProperties(stringObjectHashMap);
        WebSocketHandler sessionHandler = new TestHandler();
        WebSocketHttpHeaders webSocketHttpHeaders = new WebSocketHttpHeaders();
        webSocketHttpHeaders.add("ssid", "d6b998e76054517ccff30d20a07b0c93");
        ListenableFuture<WebSocketSession> webSocketSessionListenableFuture =
                websocketClient.doHandshake(sessionHandler, webSocketHttpHeaders, new URI(URL));

        WebSocketSession webSocketSession = webSocketSessionListenableFuture.get();
        Assert.assertTrue(webSocketSession.isOpen());

        String textMessage = "{name:\"ssid\",\"msg\":\"6259810b3881d635594a281883b4f51d\"}";
        TextMessage message1 = new TextMessage(textMessage.getBytes());
        webSocketSession.sendMessage(message1);

        TimeUnit.SECONDS.sleep(30);
//        WebSocketStompClient stompClient = new WebSocketStompClient(websocketClient);
//        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
//        StompSessionHandler sessionHandler = new AuthSessionClientHandler();
//        ListenableFuture<StompSession> connect = stompClient.connect(URL, sessionHandler);
//        StompSession stompSession = connect.get();
//        Assert.assertTrue(stompSession.isConnected());


//        WebSocketTransport webSocketTransport = new WebSocketTransport(websocketClient);
//        SockJsClient sockJsClient = new SockJsClient(Collections.<Transport>singletonList(webSocketTransport));
//        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
//
////        stompClient.setMessageConverter(new StringMessageConverter());
//        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
//
//        StompSessionHandler sessionHandler = new AuthSessionClientHandler();
//        ListenableFuture<StompSession> connect = stompClient.connect(URL, sessionHandler);
//
//        StompSession stompSession = connect.get();
//        System.out.println("sessionId: " + stompSession.getSessionId());
//
//        String path = "/echo/websocket";
//
//        stompSession.subscribe(path, new MySimpleStompFrameHandler());

        webSocketSession.close();
        log.info("test");
//        stompSession.disconnect();
    }

}
