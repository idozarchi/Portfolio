package observer;

import org.junit.Test;
import org.observer.*;

public class TestObserver {
    @Test
    public void testObserver(){

        Publisher pub = new Publisher(new Dispatcher<>());
        Subscriber sub1 = new Subscriber();
        Subscriber sub2 = new Subscriber();
        Subscriber sub3 = new Subscriber();
        Subscriber sub4 = new Subscriber();
        Subscriber sub5 = new Subscriber();

        sub1.register(pub);
        sub2.register(pub);
        sub3.register(pub);
        sub4.register(pub);
        sub5.register(pub);

        pub.notifyAllSub(5);
    }
}
