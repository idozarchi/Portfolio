package org.generichttpserver;

import com.sun.net.httpserver.HttpExchange;
import org.connectionserviceutils.IConnection;
import org.connectionserviceutils.Status;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HTTPIConnection implements IConnection {
    private final HttpExchange exchange;

    public HTTPIConnection(HttpExchange exchange) {
        this.exchange = exchange;
    }

    @Override
    public byte[] read() throws IOException {
        InputStream input = exchange.getRequestBody();
        int inputLen = input.available();

        byte[] request = new byte[inputLen];

        int bytesRead = input.read(request, 0, inputLen);

        if (bytesRead != inputLen){
            throw new IOException();
        }
        return request;
    }

    @Override
    public void write(String message, Status status) throws IOException {
        exchange.sendResponseHeaders(status.getStatusCode(), message.length());
        OutputStream response = exchange.getResponseBody();
        response.write(message.getBytes());
        response.close();
    }
}
