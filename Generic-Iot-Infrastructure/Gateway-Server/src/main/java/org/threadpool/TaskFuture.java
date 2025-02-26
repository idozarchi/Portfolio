package org.threadpool;

import java.util.concurrent.Future;

public interface TaskFuture<V> extends Future<V> {
    public boolean isRunning();
    public boolean isWaiting();
}
