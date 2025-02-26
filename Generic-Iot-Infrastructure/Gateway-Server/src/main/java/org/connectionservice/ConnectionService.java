package org.connectionservice;


import org.connectionserviceutils.EventHandler;
import org.connectionserviceutils.Handler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Set;

public class ConnectionService {
    private final Selector selector;
    private final EventHandler eventHandler;
    private boolean isRunning = true;

    public ConnectionService(EventHandler handler) throws IOException {
        eventHandler = handler;
        selector = Selector.open();
    }

    public void start() throws IOException, ClassNotFoundException {
        while (isRunning){
            if(selector.select() == 0){
                continue;
            }

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            for (SelectionKey key : selectedKeys){
                ((Handler)key.attachment()).handle();
            }
            selectedKeys.clear();
        }
    }

    public void stop(){
        isRunning = false;
    }

    public void addTCPConnection(String ipAddress, int port) throws IOException {
        ServerSocketChannel tcpChannel = ServerSocketChannel.open();
        tcpChannel.bind(new InetSocketAddress(ipAddress, port));
        tcpChannel.configureBlocking(false);
        tcpChannel.register(selector, SelectionKey.OP_ACCEPT, new TCPAcceptHandler(selector, eventHandler, tcpChannel));

        System.out.println("TCP channel added to server");
    }

    public void addUDPConnection(String ipAddress, int port) throws IOException {
        DatagramChannel udpChannel = DatagramChannel.open();
        udpChannel.bind(new InetSocketAddress(ipAddress, port));
        udpChannel.configureBlocking(false);
        udpChannel.register(selector, SelectionKey.OP_READ, new UDPReadHandler(udpChannel, eventHandler));

        System.out.println("UDP channel added to server");
    }
}
