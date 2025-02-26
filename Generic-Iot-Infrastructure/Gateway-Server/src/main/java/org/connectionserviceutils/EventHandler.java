package org.connectionserviceutils;

import java.io.IOException;

public interface EventHandler {
    public boolean onAccept(IConnection connection);
    public void onRead(IConnection connection) throws IOException, ClassNotFoundException;
    public void onWrite(IConnection connection, String message, Status status) throws IOException, ClassNotFoundException;
}
