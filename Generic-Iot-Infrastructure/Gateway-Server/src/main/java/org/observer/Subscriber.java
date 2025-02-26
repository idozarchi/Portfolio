package org.observer;

public class Subscriber {
    private final Callback<Integer> callback = new Callback<>((data)->{
        System.out.println("message is: " + data);}, ()-> {
        System.out.println("Service stopped");
    });

    public void register(Publisher publisher) {
        publisher.registerCallback(callback);
    }

    public void unregister(){
        callback.unsubscribe();
    }
}