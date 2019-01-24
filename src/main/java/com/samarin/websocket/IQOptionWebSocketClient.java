package com.samarin.websocket;

import com.samarin.websocket.exception.WebSocketSendMessageException;
import java.io.IOException;
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
   * Method creates a connection and saves a link to
   * @param url
   */
  public void connect(String url) {
    CommonMessageHandler commonMessageHandler = new CommonMessageHandler();
    connect(url, commonMessageHandler);
  }

  public void connect(String url, WebSocketHandler sessionHandler) {
    ListenableFuture<WebSocketSession> webSocketSessionListenableFuture = getWebSocketSessionListenableFuture(
        url, sessionHandler);
    webSocketSessionListenableFuture
        .addCallback(webSocketSession -> this.session = webSocketSession,
            throwable -> log.error("", throwable));
  }

  public void connectAndWait(String url) {
    CommonMessageHandler commonMessageHandler = new CommonMessageHandler();
    connectAndWait(url, commonMessageHandler);
  }

  public void connectAndWait(String url, WebSocketHandler sessionHandler) {
    ListenableFuture<WebSocketSession> webSocketSessionListenableFuture = getWebSocketSessionListenableFuture(
        url, sessionHandler);
    try {
      session = webSocketSessionListenableFuture.get();
    } catch (InterruptedException | ExecutionException e) {
      log.error("Could not connect to websocket server - {}", url, e);
    }
  }

  public void sendMessage(String message) {
    try {
      WebSocketSession session = Optional.ofNullable(this.session)
          .orElseThrow(WebSocketSendMessageException::new);
      session.sendMessage(new TextMessage(message.getBytes()));
    } catch (WebSocketSendMessageException e) {
      log.error("Connection not yet established", e);
    } catch (IOException e) {
      log.error("Cant get bytes from message", e);
    }
  }

  public void getResponses() {

  }

  public void close() {
    try {
      session.close();
    } catch (IOException e) {
      log.error("Error closing connection", e);
    }
  }

  private ListenableFuture<WebSocketSession> getWebSocketSessionListenableFuture(String url,
      WebSocketHandler sessionHandler) {
    this.handler = sessionHandler;
    StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
    return webSocketClient
        .doHandshake(sessionHandler, url);
  }
}
