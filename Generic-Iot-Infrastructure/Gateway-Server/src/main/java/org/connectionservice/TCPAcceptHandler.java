package org.connectionservice;

import org.connectionserviceutils.EventHandler;
import org.connectionserviceutils.Handler;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class TCPAcceptHandler implements Handler {
    private final Selector selector;
    private final EventHandler eventHandler;
    private final ServerSocketChannel channel;

    public TCPAcceptHandler(Selector selector, EventHandler eventHandler, ServerSocketChannel channel){
        this.selector = selector;
        this.eventHandler = eventHandler;
        this.channel = channel;
    }

    @Override
    public void handle() throws IOException {
        if(!eventHandler.onAccept(null)){
            return;
        }

        SocketChannel client = channel.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ, new TCPReadHandler(eventHandler, client));
    }
}
