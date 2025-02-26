package org.connectionservice;


import org.connectionserviceutils.EventHandler;
import org.connectionserviceutils.Handler;

import java.io.IOException;
import java.nio.channels.DatagramChannel;

public class UDPReadHandler implements Handler {
    private final EventHandler eventHandler;
    private final DatagramChannel channel;

    public UDPReadHandler(DatagramChannel channel, EventHandler eventHandler){
        this.channel = channel;
        this.eventHandler = eventHandler;
    }

    @Override
    public void handle() throws IOException, ClassNotFoundException {
        eventHandler.onRead(new UDPIConnection(channel));
    }
}
