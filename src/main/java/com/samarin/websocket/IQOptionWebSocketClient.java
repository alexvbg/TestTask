package com.samarin.websocket;

import com.samarin.websocket.exception.IQOptionWebSocketClientException;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@Slf4j
public class IQOptionWebSocketClient {

  private WebSocketSession session;
  private WebSocketHandler handler;

  /**
   * The method creates a connection to wss with the default event handler
   *
   * @param url wss uri
   */
  public void connect(String url) {
    CommonMessageHandler commonMessageHandler = new CommonMessageHandler();
    connect(url, commonMessageHandler);
  }

  /**
   * The method creates a connection to wss
   *
   * @param url wss uri
   * @param sessionHandler {@link WebSocketHandler } instance
   */
  public void connect(String url, WebSocketHandler sessionHandler) {
    ListenableFuture<WebSocketSession> webSocketSessionListenableFuture = getWebSocketSessionListenableFuture(
        url, sessionHandler);
    webSocketSessionListenableFuture
        .addCallback(webSocketSession -> this.session = webSocketSession,
            throwable -> log.error("", throwable));
  }

  /**
   * The method creates a connection to wss with the default event handler and waits until the
   * session is initialized
   *
   * @param url wss uri
   */
  public void connectAndWait(String url) {
    CommonMessageHandler commonMessageHandler = new CommonMessageHandler();
    connectAndWait(url, commonMessageHandler);
  }

  /**
   * The method creates a connection to wss and waits until the session is initialized
   *
   * @param sessionHandler {@link WebSocketHandler } instance
   * @param url wss uri
   */
  public void connectAndWait(String url, WebSocketHandler sessionHandler) {
    ListenableFuture<WebSocketSession> webSocketSessionListenableFuture = getWebSocketSessionListenableFuture(
        url, sessionHandler);
    try {
      session = webSocketSessionListenableFuture.get();
    } catch (InterruptedException | ExecutionException e) {
      log.error("Could not connect to websocket server - {}", url, e);
    }
  }

  /**
   * Method to close the connection
   */
  public void close() {
    try {
      session.close();
    } catch (IOException e) {
      log.error("Error closing connection", e);
    }
  }


  public boolean isConnectionOpen() {
    return session.isOpen();
  }

  /**
   * Method allows you to send a text message to the channel.
   * Session may not be set yet, therefore used Optional.ofNullable
   */
  public void sendMessage(String message) {
    try {
      WebSocketSession session = Optional.ofNullable(this.session)
          .orElseThrow(() -> new IQOptionWebSocketClientException(
              String.format("Cant send message - %s", message)));
      log.debug("Send message - {}", message);
      session.sendMessage(new TextMessage(message.getBytes()));
    } catch (IQOptionWebSocketClientException e) {
      log.error("Connection not yet established", e);
    } catch (IOException e) {
      log.error("Cant get bytes from message", e);
    }
  }

  private ListenableFuture<WebSocketSession> getWebSocketSessionListenableFuture(String url,
      WebSocketHandler sessionHandler) {
    this.handler = sessionHandler;
    StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
    return webSocketClient
        .doHandshake(sessionHandler, url);
  }

  public Map getResponses() throws IQOptionWebSocketClientException {
    if (handler instanceof ResponseMessageAccumulator) {
      return ((ResponseMessageAccumulator) handler).getResponses();
    }
    throw new IQOptionWebSocketClientException(
        "Cant get responses from handler, you must implement ResponseMessageAccumulator");
  }

  public void clearResponses() throws IQOptionWebSocketClientException {
    if (handler instanceof ResponseMessageAccumulator) {
      ((ResponseMessageAccumulator) handler).clearResponses();
    } else {
      throw new IQOptionWebSocketClientException(
          "Cant clear responses from handler, you must implement ResponseMessageAccumulator");
    }
  }
}
