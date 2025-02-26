package org.connectionservice;

import org.connectionserviceutils.EventHandler;
import org.connectionserviceutils.Handler;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class TCPReadHandler implements Handler {
    private final EventHandler eventHandler;
    private final SocketChannel channel;

    public TCPReadHandler(EventHandler eventHandler, SocketChannel channel){
        this.eventHandler = eventHandler;
        this.channel = channel;
    }

    @Override
    public void handle() throws IOException, ClassNotFoundException {
        eventHandler.onRead(new TCPIConnection(channel));
    }
}
