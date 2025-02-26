package org.plugnplay;



import org.observer.Callback;
import org.observer.Dispatcher;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class DirWatcher {
    private final Dispatcher<List<WatchEvent<?>>> dispatcher;
    private final WatchService watcher;
    private boolean isThreadRunning = true;

    public DirWatcher(Path jarFolder) throws IOException {
        watcher = FileSystems.getDefault().newWatchService();
        jarFolder.register(watcher, ENTRY_CREATE, ENTRY_MODIFY);
        dispatcher = new Dispatcher<>();
    }

    public void registerCallback(Callback<List<WatchEvent<?>>> call) {
        dispatcher.subscribe(call);
    }

    public void stopService() throws IOException {
        isThreadRunning = false;
        dispatcher.stopService();
        watcher.close();
    }

    public void start() {
        new Thread(new WatchRunner()).start();
    }

    private class WatchRunner implements Runnable{

        @Override
        public void run() {
            WatchKey key = null;

            while(isThreadRunning){
                try {
                    key = watcher.take();
                } catch (InterruptedException e) {
                    continue;
                }
                catch (ClosedWatchServiceException closedWatchServiceException) {break;}

                dispatcher.publish(key.pollEvents());
                /*
                for(WatchEvent<?> watch : key.pollEvents()){
                    dispatcher.publish(watch);
                }*/

                key.reset();
            }
        }
    }
}

