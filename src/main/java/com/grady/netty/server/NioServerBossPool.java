package com.grady.netty.server;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

public class NioServerBossPool<T extends Boss> {

    private final Boss[] bosses;

    private final AtomicInteger bossIndex = new AtomicInteger();

    private final Executor bossExecutor;


    public NioServerBossPool(Executor bossExecutor, int bossCount, NioServerSocketChannelFactory factory) {
        this.bossExecutor = bossExecutor;
        bosses = new Boss[bossCount];

        for (int i = 0; i < bosses.length; i++) {
            bosses[i] = new NioServerBoss(bossExecutor, "boss thread " + (i+1), factory);
        }
    }

    public Boss nextBoss() {
        return bosses[Math.abs(bossIndex.getAndIncrement() % bosses.length)];
    }
}
