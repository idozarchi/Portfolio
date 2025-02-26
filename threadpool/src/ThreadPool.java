/*TODO
 *  Ido Zarchi
 *  Reviewer: Vlada
 * */

package org.threadpool;

import org.jetbrains.annotations.NotNull;
import org.waitablepq.WaitablePQFixedSize;

import java.util.ArrayList;
import java.util.concurrent.*;

public class ThreadPool implements Executor {
    private final int Factor_PQ = 1;
    private ArrayList<Routine> threads = null;
    private final WaitablePQFixedSize<Task<?>> pq;
    private final Semaphore pause;
    private boolean isShutdown = false;
    private boolean isPaused = false;
    private final Semaphore deadThreadsSem = new Semaphore(0);
    private final Callable<?> poisonedApple;
    private final Callable<?> sleepingPill;

    {
        pause = new Semaphore(0);
        poisonedApple = ()->{((Routine)Thread.currentThread()).keepRunning = false;
            deadThreadsSem.release(); return null;};
        sleepingPill = ()->{pause.acquire(); return null;};
    }

    public ThreadPool(int size){
        if(size == 0){
            size = Runtime.getRuntime().availableProcessors();
        }

        pq = new WaitablePQFixedSize<>(size * Factor_PQ); //TODO change pq size
        threads = new ArrayList<>(size);
        addNThreads(size);
    }

    private  <V> Future<V> mySubmit(Callable<V> command, Integer priority) {
        Task<V> task = new Task<>(pq, command, priority);
        try {
            pq.enqueue(task);
        } catch (InterruptedException ignored) {}

        return task.getFuture();
    }

    public <V> Future<V> submit(Callable<V> command, Priority priority) {
        if(isShutdown){
            throw new RejectedExecutionException();
        }
        return mySubmit(command, priority.ordinal());
    }

    public <V> Future<V> submit(Callable<V> command){
        return submit(command, Priority.DEFAULT);
    }

    public <V> Future<V> submit(Runnable command, Priority priority, V outParam) {
        Callable<V> call = Executors.callable(command, outParam);
        return submit(call, priority);
    }

    public <V> Future<V> submit(Runnable command, Priority priority){
        return submit(command, priority, null);
    }

    @Override
    public void execute(@NotNull Runnable command) {
        submit(command, Priority.DEFAULT, null);
    }

    public void setNumOfThreads(int numOfThreads) throws InterruptedException {
        int diff = threads.size() - numOfThreads;
        if(0 > diff){
            addNThreads(diff * -1);
            return;
        }

        if(diff > threads.size()){
            diff = threads.size();
        }
        endNThreads(diff, Priority.HIGH.ordinal() + 1);
        cleanupThreadsContainer(diff);
    }

    public int getNumOfThreads(){
        return threads.size();
    }

    public void pauseTP(){
        isPaused = true;
        for(int i = 0; i < threads.size(); ++i){
            mySubmit(sleepingPill , Priority.HIGH.ordinal() + 1);
        }
    }

    public void resumeTP(){
        pause.release(threads.size());
        isPaused = false;
    }

    public void shutDown() throws InterruptedException {
        if(isShutdown){
            return;
        }

        isShutdown = true;
        endNThreads(threads.size(), Priority.LOW.ordinal() - 1);
    }

    public void awaitTermination() throws InterruptedException {
        cleanupThreadsContainer(threads.size());
    }

    public boolean awaitTermination(long timeOut) throws InterruptedException {
        return deadThreadsSem.tryAcquire(threads.size(), timeOut, TimeUnit.MILLISECONDS);
    }

    private void addNThreads(int nThreads) {
        for (int i = 0; i < nThreads; ++i){
            Routine thread = new Routine();
            if(isPaused){
                mySubmit(sleepingPill, Priority.HIGH.ordinal() + 1);
            }
            threads.add(thread);
            thread.start();
        }
    }

    private void endNThreads(int nThreads, int priority) throws InterruptedException {
        for(int i = 0; i < nThreads; ++i){
            mySubmit(poisonedApple, priority);
        }
    }

    private void cleanupThreadsContainer(int nThreads) throws InterruptedException {
        deadThreadsSem.acquire(nThreads);
        while (0 < nThreads) {
            for(int i = 0; i < threads.size(); ++i){
                if(!threads.get(i).isAlive()){
                    threads.remove(i);
                    --nThreads;
                }
            }
        }
    }

    private class Routine extends Thread{
        boolean keepRunning = true;

        @Override
        public void run(){
            Task<?> t = null;
            while (keepRunning){
                try {
                    t = pq.dequeue();
                    t.run();
                } catch (Exception e) {
                    t.getFuture().setException(e);
                }
            }
        }
    }

    public enum Priority {
        LOW,
        DEFAULT,
        HIGH;
    }

    private enum Status {
        UNDONE,
        DONE,
        CANCELLED
    }

    private static class Task<V> implements Comparable<Task<?>> {
        private final TFuture future;
        private final Integer priority;
        private final Callable<V> method;
        private final WaitablePQFixedSize<Task<?>> pq;
        private final Semaphore getSem = new Semaphore(0);

        private Task(WaitablePQFixedSize<Task<?>> pq, Callable<V> method, Integer p){
            this.method = method;
            this.priority = p;
            this.pq = pq;
            this.future = new TFuture();
        }

        private TFuture getFuture(){
            return future;
        }

        private int getPriority(){
            return priority;
        }

        private Future<V> run() throws Exception {
            future.setReturnVal(method.call());
            future.setCurrentStatusDone();
            getSem.release();

            return future;
        }

        @Override
        public int compareTo(@NotNull Task<?> task) {
            return task.getPriority() - getPriority();
        }

        private class TFuture implements Future<V>{
            private Status currentStatus;
            private Exception ex;
            private V returnVal;

            {
                currentStatus = Status.UNDONE;
                ex = null;
                returnVal = null;
            }

            @Override
            public boolean cancel(boolean b){
                if(currentStatus.equals(Status.DONE) || !Task.this.pq.remove(Task.this)){
                    return false;
                }

                currentStatus = Status.CANCELLED;
                return true;
            }

            @Override
            public boolean isCancelled(){
                return currentStatus.equals(Status.CANCELLED);
            }

            @Override
            public boolean isDone(){
                return currentStatus.equals(Status.DONE);
            }

            @Override
            public V get() throws InterruptedException, ExecutionException{
                if(ex != null){
                    throw new ExecutionException(ex);
                }
                getSem.acquire();

                return returnVal;
            }

            @Override
            public V get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException{
                if(ex != null){
                    throw new ExecutionException(ex);
                }

                if (getSem.tryAcquire(l, timeUnit)){
                    return returnVal;
                }

                throw new TimeoutException();
            }

            private void setCurrentStatusDone(){
                currentStatus = Status.DONE;
            }

            private void setException(Exception exeption){
                ex = exeption;
            }

            private void setReturnVal(V v){
                returnVal = v;
            }
        }
    }
}
