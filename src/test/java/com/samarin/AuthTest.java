package com.samarin;


import com.samarin.websocket.IQOptionWebSocketClient;
import com.samarin.websocket.TestHandler;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketHandler;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


@Slf4j
public class AuthTest {

  IQOptionWebSocketClient webSocketClient;

  private static String URL = "wss://iqoption.com/echo/websocket";

  @BeforeClass
  public void beforeTest() {
    WebSocketHandler sessionHandler = new TestHandler();
    webSocketClient = new IQOptionWebSocketClient();
    webSocketClient.connect(URL, sessionHandler);
  }

  @Test
  public void test()
      throws InterruptedException, ExecutionException, IOException, URISyntaxException {

    String message = "";

    webSocketClient.sendMessage(message);

    webSocketClient.getResponses();

    webSocketClient.close();

  }

}
