package com.samarin.websocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.samarin.utils.json.MessageDeserializer;
import com.samarin.websocket.model.Message;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * Default handler for wss connection.
 * Allows you to accumulate all messages from the server by their types
 */
@Slf4j
public class CommonMessageHandler implements WebSocketHandler, ResponseMessageAccumulator {

  private Gson gson;
  private HashMap<String, List<Message>> responses = new HashMap<>();

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    GsonBuilder gsonBuilder = new GsonBuilder();
    MessageDeserializer messageDeserializer = new MessageDeserializer();
    gsonBuilder.registerTypeAdapter(Message.class, messageDeserializer);
    gson = gsonBuilder.create();
  }

  @Override
  public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
    String textMessage = message.getPayload().toString();
    log.debug("handleMessage - {}", textMessage);
    Message messageEntity = gson.fromJson(textMessage, Message.class);
    if (responses.putIfAbsent(messageEntity.getName(),
        new ArrayList<>(Collections.singletonList(messageEntity))) != null) {
      responses.get(messageEntity.getName()).add(messageEntity);
    }
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) {
    log.error("Transport error", exception);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
    log.debug("Connection closed, status - {}, reason - {}", closeStatus.getCode(),
        closeStatus.getReason());
  }

  @Override
  public boolean supportsPartialMessages() {
    return false;
  }

  public Map getResponses() {
    return responses;
  }

  @Override
  public void clearResponses() {
    responses = new HashMap<>();
  }
}
