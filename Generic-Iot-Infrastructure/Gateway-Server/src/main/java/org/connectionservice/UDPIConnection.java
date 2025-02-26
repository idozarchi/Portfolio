package org.connectionservice;


import org.connectionserviceutils.IConnection;
import org.connectionserviceutils.Status;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class UDPIConnection implements IConnection {
    private final DatagramChannel channel;
    private SocketAddress clientAddress;
    private final ByteBuffer buffer = ByteBuffer.allocate(1024);

    public UDPIConnection(DatagramChannel channel) {
        this.channel = channel;
    }

    @Override
    public byte[] read() throws IOException {
        clientAddress = channel.receive(buffer);
        buffer.flip();

        return buffer.array();
    }

    @Override
    public void write(String message, Status status) throws IOException {
        buffer.put((message + status.name()).getBytes());
        buffer.flip();
        channel.send(buffer, clientAddress);
    }
}
