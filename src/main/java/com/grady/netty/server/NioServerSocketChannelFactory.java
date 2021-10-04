package com.grady.netty.server;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public class NioServerSocketChannelFactory {

    private final NioServerWorkerPool<NioServerWorker> workerPool;

    private final NioServerBossPool<NioServerBoss> bossPool;
    private final AtomicInteger bossIndex = new AtomicInteger();



    public NioServerSocketChannelFactory(Executor bossExecutor, Executor workerExecutor) {
        workerPool = new NioServerWorkerPool(workerExecutor, getMaxThreads(workerExecutor), this);
        bossPool = new NioServerBossPool(bossExecutor, 1, this);
    }

    public NioServerSocketChannelFactory(Executor bossExecutor,
                                         int bossCount,
                                         Executor workerExecutor,
                                         int workerCount) {
        workerPool = new NioServerWorkerPool(workerExecutor, workerCount, this);
        bossPool = new NioServerBossPool(bossExecutor, bossCount, this);
    }

    public Boss nextBoss() {
        return bossPool.nextBoss();
    }

    public Worker nextWorker() {
        return workerPool.nextWorker();
    }

    private static int getMaxThreads(Executor executor) {
        if (executor instanceof ThreadPoolExecutor) {
            final int maxThreads = ((ThreadPoolExecutor) executor).getMaximumPoolSize();
            return Math.min(maxThreads, Runtime.getRuntime().availableProcessors() * 2);
        }
        return Runtime.getRuntime().availableProcessors() * 2;
    }
}
