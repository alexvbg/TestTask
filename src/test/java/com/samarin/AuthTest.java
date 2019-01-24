package com.samarin;


import com.samarin.websocket.IQOptionWebSocketClient;
import com.samarin.websocket.exception.IQOptionWebSocketClientException;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;


@Slf4j
public class AuthTest {

    IQOptionWebSocketClient webSocketClient;

    private static String URL = "wss://iqoption.com/echo/websocket";

    @BeforeClass
    public void beforeTest() {
        webSocketClient = new IQOptionWebSocketClient();
        webSocketClient.connectAndWait(URL);
    }

    @Test
    public void test() throws InterruptedException, IQOptionWebSocketClientException {
        String text = "{\"name\":\"ssid\",\"msg\":\"6259810b3881d635594a281883b4f51d\"}";
        webSocketClient.sendMessage(text);
        TimeUnit.SECONDS.sleep(5);
        log.info(String.valueOf(webSocketClient.getResponses().keySet().size()));
        webSocketClient.close();
    }

}
