package com.samarin.websocket;

import java.util.Map;

/**
 * Interface which says that the handler accumulates responses from the server.
 * Can be used for customization of response processors in test framework
 */
public interface ResponseMessageAccumulator {

    Map getResponses();

    void clearResponses();
}
