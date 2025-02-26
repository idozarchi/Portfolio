package org.plugnplay;

import org.factorydp.Factory;
import org.observer.Callback;
import org.rps.command.Command;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.util.List;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

public class PlugAndPlay {
    private final Factory<String, Map<String, String>, Command> factory;
    private final DirWatcher dWatcher;
    private final String folderPath;


    public PlugAndPlay(String folderPath, Factory<String, Map<String, String>, Command> factory) throws IOException {
        this.factory = factory;
        this.folderPath = folderPath;
        dWatcher = new DirWatcher(FileSystems.getDefault().getPath(folderPath));
    }

    public void start() {
        dWatcher.registerCallback(new Callback<>(this::handleEvent, this::stop));
        dWatcher.start();
    }

    public void handleEvent(List<WatchEvent<?>> events) {
        for(WatchEvent<?> event : events){
            if (event.kind() == OVERFLOW ) {
                continue;
            }

            Path filePath = (Paths.get(folderPath + "/" + event.context()));

            if (!JarValidator.validate(filePath.toString())) {
                continue;
            }

            DynamicJarLoader djr = new DynamicJarLoader(Command.class.getName());
            List<Class<?>> classes = djr.load(filePath.toString());

            for (Class<?> c : classes) {
                System.out.println("the name is: " + c.getName());
                factory.add(c.getName(), (args) -> {
                    try {
                        return (Command) c.getDeclaredConstructor(Map.class).newInstance(args);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }

    public void stop() {
        try {
            dWatcher.stopService();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
