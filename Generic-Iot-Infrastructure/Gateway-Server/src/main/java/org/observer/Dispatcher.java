package org.observer;

import java.util.ArrayList;

public class Dispatcher<T> {
    private final ArrayList<Callback<T>> callbacks = new ArrayList<>();

    public void publish(T data){
        for(Callback<T> callback : callbacks){
            callback.update(data);
        }
    }

    public void subscribe(Callback<T> callback){
        callbacks.add(callback);
        callback.setDispatcher(this);
    }

    public void unsubscribe(Callback<T> callback){
        callbacks.remove(callback);
        callback.setDispatcher(null);
    }

    public void stopService(){
        for(Callback<T> callback : callbacks){
            callback.stopService();
            callback.setDispatcher(null);
        }
        callbacks.clear();
    }
}
