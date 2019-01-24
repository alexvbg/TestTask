package com.samarin;


import com.samarin.websocket.AuthSessionClientHandler;
import com.samarin.websocket.MySimpleStompFrameHandler;
import com.samarin.websocket.TestHandler;
import com.samarin.websocket.WebsocketClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


@Slf4j
public class AuthTest {

    private static String URL = "wss://iqoption.com/echo/websocket";

    @BeforeClass
    public void beforeTest() {
    }

    @Test
    public void test() throws InterruptedException, ExecutionException, IOException {

        WebSocketClient websocketClient = new StandardWebSocketClient();
        WebSocketHandler sessionHandler = new TestHandler();
        ListenableFuture<WebSocketSession> webSocketSessionListenableFuture =
                websocketClient.doHandshake(sessionHandler, URL);

        WebSocketSession webSocketSession = webSocketSessionListenableFuture.get();
        Assert.assertTrue(webSocketSession.isOpen());


        TimeUnit.SECONDS.sleep(10);
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
