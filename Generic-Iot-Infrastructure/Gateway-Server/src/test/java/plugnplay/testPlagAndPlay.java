package plugnplay;

import org.factorydp.Factory;
import org.junit.Test;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.plugnplay.PlugAndPlay;
import org.rps.command.Command;

public class testPlagAndPlay {
    @Test
    public void puckThemAll() throws IOException, InterruptedException {
        Factory<String, Map<String,String>, Command> factory = new Factory<>();
        PlugAndPlay pnp = new PlugAndPlay("/home/idozz/Desktop/first-git-project/java/projects/src/il/co/ilrd/plugnplay",
                factory);
        pnp.start();
        Map<String, String> arguments = new HashMap<>();
        arguments.put("CompanyName", "Infinifinifinity");
        arguments.put("CompanyID", "666");
        System.out.println("All ready");

        Thread.sleep(10_000);
        Command someCommand = factory.create("il.co.ilrd.plugnplay.ClassLoaderTest$testImplementsCommand", arguments);
        someCommand.execute();
    }
}
