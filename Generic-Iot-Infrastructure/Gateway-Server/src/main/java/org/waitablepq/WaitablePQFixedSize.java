package org.waitablepq;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;

public class WaitablePQFixedSize<E> {
    private final PriorityQueue<E> pq;
    private final Semaphore read;
    private final Semaphore write;

    public WaitablePQFixedSize(int numOfElements){
        pq = new PriorityQueue<>();

        read = new Semaphore(0);
        write = new Semaphore(numOfElements);
    };

    public WaitablePQFixedSize(Comparator <? super E> cmp, int numOfElements){
        pq = new PriorityQueue<>(cmp);

        read = new Semaphore(0);
        write = new Semaphore(numOfElements);
    }

    public boolean isEmpty(){
        return pq.isEmpty();
    }

    public void enqueue(E element) throws InterruptedException {
        write.acquire();
        synchronized (pq){
            pq.add(element);
        }
        read.release();
    }

    public E dequeue() throws InterruptedException {
        E element = null;
        read.acquire();
        synchronized (pq){
            element = pq.poll();
        }
        write.release();

        return element;
    }

    public boolean remove(Object o) {
        if(read.tryAcquire()){
            synchronized (pq){
                if(pq.remove(o)){
                    write.release();
                    return true;
                }
                read.release();
            }
        }

        return false;
    }
}