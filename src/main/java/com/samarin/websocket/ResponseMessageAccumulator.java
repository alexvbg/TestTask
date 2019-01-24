package com.samarin.websocket;

import java.util.Map;

public interface ResponseMessageAccumulator<K, V> {

  Map<K, V> getResponses();

}
