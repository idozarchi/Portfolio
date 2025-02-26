package org.observer;

public class Publisher {
    private final Dispatcher<Integer> dis;

    public Publisher(Dispatcher<Integer> dis){
        this.dis = dis;
    }

    public void registerCallback(Callback<Integer> call){
        dis.subscribe(call);
    }

    public void unregisterCallback(Callback<Integer> call){
        dis.unsubscribe(call);
    }

    public void notifyAllSub(Integer data){
        dis.publish(data);
    }

    public void stopService(){
        dis.stopService();
    }
    
}
