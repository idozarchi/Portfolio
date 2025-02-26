package org.generichttpserver;


import org.connectionserviceutils.IConnection;

import java.io.IOException;

public interface Controller {
    void handle(IConnection connection) throws IOException, ClassNotFoundException;
}
