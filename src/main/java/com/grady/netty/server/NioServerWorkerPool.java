package com.grady.netty.server;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

public class NioServerWorkerPool<T extends Worker> {

    private final Worker[] workers;

    private final AtomicInteger workerIndex = new AtomicInteger();

    private final Executor workerExecutor;

    public NioServerWorkerPool(Executor workerExecutor, int workCount, NioServerSocketChannelFactory factory) {
        this.workerExecutor = workerExecutor;
        workers = new Worker[workCount];

        for (int i = 0; i < workers.length; i++) {
            workers[i] = new NioServerWorker(workerExecutor, "worker thread " + (i+1), factory);
        }
    }


    public Worker nextWorker() {
        return workers[Math.abs(workerIndex.getAndIncrement() % workers.length)];
    }
}
