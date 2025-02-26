package org.rps.command;

import java.util.Map;

public class RegDevice implements Command{
    private final String deviceName;

    public RegDevice(Map<String, String> args) {
        deviceName = args.get("deviceName");
    }

    @Override
    public void execute() {
        System.out.println("Device: " + deviceName);
    }
}

