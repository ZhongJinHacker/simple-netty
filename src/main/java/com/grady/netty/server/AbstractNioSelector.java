package com.grady.netty.server;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractNioSelector implements Runnable {

    /**
     * 线程池
     */
    private final Executor executor;

    /**
     * 选择器
     */
    protected Selector selector;

    /**
     * 选择器wakenUp状态标记, 判断是否阻塞中
     */
    protected final AtomicBoolean wakenUp = new AtomicBoolean();

    /**
     * 任务队列
     */
    private final Queue<Runnable> taskQueue = new ConcurrentLinkedQueue<>();

    /**
     * 线程名称
     */
    private String threadName;

    private NioServerSocketChannelFactory factory;

    public AbstractNioSelector(Executor executor, String threadName, NioServerSocketChannelFactory factory) {
        this.executor = executor;
        this.threadName = threadName;
        this.factory = factory;
        openSelector();
    }

    private void openSelector() {
        try {
            this.selector = Selector.open();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create a selector.");
        }
        this.executor.execute(this);
    }

    @Override
    public void run() {
        Thread.currentThread().setName(this.threadName);
        System.out.println("curent work thead: " + Thread.currentThread().getName());


        while (true) {
            try {
                wakenUp.set(false);
                select(selector);
                processTaskQueue();
                process(selector);
            } catch (Exception e) {
                // ignore
                e.printStackTrace();
            }
        }
    }

    private void processTaskQueue() {
        while (true) {
            final Runnable task = taskQueue.poll();
            if (task == null) {
                break;
            }
            task.run();
        }
    }

    protected final void registerTask(Runnable task) {
        taskQueue.offer(task);
        if (this.selector != null) {
            if (wakenUp.compareAndSet(false, true)) {
                selector.wakeup();
            }
        } else {
            taskQueue.remove(task);
        }
    }

    protected NioServerSocketChannelFactory getFactory() {
        return this.factory;
    }


    /**
     * select抽象方法,子类实现select策略
     *
     * @param selector
     * @return
     * @throws IOException
     */
    protected abstract int select(Selector selector) throws IOException;

    /**
     * selector的业务处理
     *
     * @param selector
     * @throws IOException
     */
    protected abstract void process(Selector selector) throws IOException;
}
