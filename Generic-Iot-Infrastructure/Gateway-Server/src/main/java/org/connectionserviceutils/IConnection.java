package org.connectionserviceutils;

import java.io.IOException;

public interface IConnection {
    public byte[] read() throws IOException;
    public void write(String message, Status status) throws IOException;
}
