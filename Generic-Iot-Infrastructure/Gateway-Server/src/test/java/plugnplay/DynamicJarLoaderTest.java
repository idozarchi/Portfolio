package plugnplay;

import org.junit.jupiter.api.Test;
import org.plugnplay.DynamicJarLoader;

import java.io.IOException;

public class DynamicJarLoaderTest {
    @Test
    public void test() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        DynamicJarLoader d = new DynamicJarLoader("il.co.ilrd.rps.command.Command");
        d.load("/home/idozz/Desktop/first-git-project/java/projects/src/testJar.jar");
    }
}
