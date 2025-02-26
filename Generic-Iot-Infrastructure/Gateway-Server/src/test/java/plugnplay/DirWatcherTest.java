package plugnplay;


import org.junit.jupiter.api.Test;
import org.observer.Callback;
import org.plugnplay.DirWatcher;

import java.io.IOException;
import java.nio.file.FileSystems;

class DirWatcherTest {

    @Test
    void start() throws IOException, InterruptedException {
        DirWatcher watcher = new DirWatcher(FileSystems.getDefault().getPath("/home/idozz/Desktop/first-git-project/java/projects/src/il/co/ilrd/plugnplay"));
        watcher.registerCallback(new Callback<>((d)->{
            //System.out.println(((Path)d.context()).getFileName());
        }, ()->{
            System.out.println("Stopped Service");
        }));

        watcher.start();

        Thread.sleep(15000);

        watcher.stopService();
    }
}