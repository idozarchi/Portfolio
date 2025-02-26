package org.gatewayserver;

import com.google.gson.JsonObject;
import org.connectionserviceutils.EventHandler;
import org.connectionserviceutils.IConnection;
import org.connectionserviceutils.Status;

import java.io.IOException;

public class Message {
    private final EventHandler eventHandler;
    private final IConnection connection;
    private final JsonObject request;

    public Message(JsonObject request, EventHandler handler, IConnection connection) {
        eventHandler = handler;
        this.connection = connection;
        this.request = request;
    }

    public JsonObject getRequest(){
        return request;
    }

    public void sendFeedback(String message, Status status) throws IOException, ClassNotFoundException {
        eventHandler.onWrite(connection, message, status);
    }

}
