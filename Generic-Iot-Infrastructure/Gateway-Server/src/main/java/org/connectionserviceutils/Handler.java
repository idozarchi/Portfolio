package org.connectionserviceutils;

import java.io.IOException;

public interface Handler {
    public void handle() throws IOException, ClassNotFoundException;
}

