package com.samarin.websocket.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.samarin.websocket.model.Message;

import java.lang.reflect.Type;

public class MessageDeserializer implements JsonDeserializer<Message> {
    @Override
    public Message deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonElement name = jsonElement.getAsJsonObject().get("name");
        JsonElement msg = jsonElement.getAsJsonObject().get("msg");
        return Message.builder().name(name.getAsString()).msg(msg.toString()).build();
    }
}
