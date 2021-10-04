package com.grady.netty.server;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class NioEventLoopGroup {

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public int getNThread() {
        return nThread;
    }

    private ExecutorService executorService = Executors.newCachedThreadPool();

    private int nThread;

    public NioEventLoopGroup(int nThread) {
        this.nThread = nThread;
    }

    public NioEventLoopGroup() {
        this.nThread = getMaxThreads(executorService);
    }

    private static int getMaxThreads(Executor executor) {
        if (executor instanceof ThreadPoolExecutor) {
            final int maxThreads = ((ThreadPoolExecutor) executor).getMaximumPoolSize();
            return Math.min(maxThreads, Runtime.getRuntime().availableProcessors() * 2);
        }
        return Runtime.getRuntime().availableProcessors() * 2;
    }
}
