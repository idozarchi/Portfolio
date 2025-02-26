package org.gatewayserver;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.connectionservice.ConnectionService;
import org.connectionserviceutils.EventHandler;
import org.connectionserviceutils.IConnection;
import org.connectionserviceutils.Status;
import org.generichttpserver.Controller;
import org.generichttpserver.GenericHTTPServer;
import org.rps.SimpleRps;

import java.io.IOException;

public class GatewayServer {
    private final SimpleRps rps = new SimpleRps();
    private final ConnectionService connectionService;
    private GenericHTTPServer httpServer;
    private final ServerEventHandler eventHandler = new ServerEventHandler();
    private final HTTPController controller = new HTTPController();

    public GatewayServer(String address, int tcpPort, int udpPort) throws IOException, ClassNotFoundException {
        connectionService = new ConnectionService(eventHandler);
        connectionService.addTCPConnection(address, tcpPort);
        connectionService.addUDPConnection(address, udpPort);
        httpServerInit();

        httpServer.start();
        connectionService.start();
    }

    private void httpServerInit() throws IOException {
        httpServer = new GenericHTTPServer();
        httpServer.addRoute("/iots", controller);
    }

    private class ServerEventHandler implements EventHandler {

        @Override
        public boolean onAccept(IConnection connection) {
            return true;
        }

        @Override
        public void onRead(IConnection connection) throws IOException, ClassNotFoundException {
            byte[] input;
            JsonObject jo;

            try {
                input = connection.read();
            } catch (IOException e){
                return;
            }
            if (input == null){
                return;
            }
            jo = JsonParser.parseString(new String(input).trim()).getAsJsonObject();
            Message message = new Message(jo, this, connection);
            rps.handleRequest(message);
        }

        @Override
        public void onWrite(IConnection connection, String message, Status status) throws IOException, ClassNotFoundException {
            connection.write(message, status);
        }
    }

    public class HTTPController implements Controller {
        @Override
        public void handle(IConnection connection) throws IOException, ClassNotFoundException {
            System.out.println("In handler");
            eventHandler.onRead(connection);
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        GatewayServer server = new GatewayServer("localhost", 12345, 12346);
    }
}
