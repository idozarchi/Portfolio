package org.generichttpserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class GenericHTTPServer {
    private final HttpServer server;

    public GenericHTTPServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(8090), 0);
    }

    public void addRoute(String url, Controller controller) {
        server.createContext(url, new GenericHTTPHandler(controller));
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    private static class GenericHTTPHandler implements HttpHandler {
        private final Controller controller;

        public GenericHTTPHandler(Controller controller) {
            this.controller = controller;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            try {
                controller.handle(new HTTPIConnection(httpExchange));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

