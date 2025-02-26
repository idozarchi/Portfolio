package org.rps;

import org.connectionserviceutils.Status;
import org.factorydp.Factory;
import org.gatewayserver.Message;
import org.rps.Parser.JSONParser;
import org.rps.command.*;
import org.threadpool.ThreadPool;

import java.io.IOException;
import java.util.Map;

public class SimpleRps{
    private final ThreadPool tp;
    private final Factory<String, Map<String, String>, Command> factory;

    public SimpleRps(){
        tp = new ThreadPool(10);
        factory = new Factory<>();

        factory.add("RegCompany", RegCompany::new);
        factory.add("RegProduct", RegProduct::new);
        factory.add("RegDevice", RegDevice::new);
        factory.add("RegUpdate", RegUpdate::new);
    }

    public void handleRequest(Message message) {
        tp.submit(()-> {
            Map<String, String> args = new JSONParser().parse(message.getRequest());
            if(args == null){
                message.sendFeedback("Invalid request", Status.BAD_REQUEST);
                return null;
            }

            getCommand(args, message);
            return null;
        });
    }

    private void getCommand(Map<String, String> args, Message message) throws IOException, ClassNotFoundException {
        Command command = factory.create(args.get("command"), args);
        if(command == null){
            message.sendFeedback("No such request", Status.REQUEST_NOT_FOUND);
            return;
        }

        tp.submit(()-> {
            command.execute();
            message.sendFeedback("Request fulfilled", Status.SUCCESS);
            return null;
        });
    }
}
