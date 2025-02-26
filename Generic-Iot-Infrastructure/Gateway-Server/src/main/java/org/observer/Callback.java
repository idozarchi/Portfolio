package org.observer;

import java.util.function.Consumer;

public class Callback<T> {
    private Dispatcher<T> dis;
    private final Consumer<T> method;
    private final Runnable stopService;

    public Callback(Consumer<T> method, Runnable stopService){
        this.method = method;
        this.stopService = stopService;
    }

    public void update(T data){
        method.accept(data);
    }

    public void unsubscribe(){
        dis.unsubscribe(this);
        dis = null;
    }

    public void setDispatcher(Dispatcher<T> dis){
        this.dis = dis;
    }

    public void stopService() {
        stopService.run();
    }
}
