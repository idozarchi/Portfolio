package org.connectionservice;

import org.connectionserviceutils.IConnection;
import org.connectionserviceutils.Status;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class TCPIConnection implements IConnection {
    private final SocketChannel channel;
    private final ByteBuffer buffer = ByteBuffer.allocate(1024);

    public TCPIConnection(SocketChannel channel){
        this.channel = channel;
    }

    @Override
    public byte[] read() throws IOException {
        int bytesRead = channel.read(buffer);

        if (-1 == bytesRead || 0 == bytesRead) {
            System.out.println("The motherfucker channel closed unexpectedly!");
            channel.close();
            return null;
        }

        buffer.flip();
        return buffer.array();
    }

    @Override
    public void write(String message, Status status) throws IOException {
        buffer.put((message + status.name()).getBytes());
        buffer.flip();
        channel.write(buffer);
    }
}
