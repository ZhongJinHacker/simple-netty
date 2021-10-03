package com.grady.netty.server;

import java.io.IOException;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;

public class NioServerBoss extends AbstractNioSelector implements Boss {

    public NioServerBoss(Executor bossExecutor, String threadName, NioServerSocketChannelFactory factory) {
        super(bossExecutor, threadName, factory);
    }

    @Override
    protected int select(Selector selector) throws IOException {
        return selector.select();
    }

    @Override
    protected void process(Selector selector) throws IOException {
        Set<SelectionKey> selectedKeys = selector.selectedKeys();
        if (selectedKeys.isEmpty()) {
            return;
        }
        for (Iterator<SelectionKey> i = selectedKeys.iterator(); i.hasNext();) {
            SelectionKey key = i.next();
            i.remove();
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            SocketChannel channel = serverSocketChannel.accept();
            channel.configureBlocking(false);
            Worker nextworker = getFactory().nextWorker();
            // 此channel注册到worker的selector上
            nextworker.registerNewChannelTask(channel);

            System.out.println("新客户端链接");
        }
    }

    // boss 处理serverChannel 注册Accept事件
    public void registerAcceptChannelTask(ServerSocketChannel serverChannel) {
        final Selector selector = this.selector;
        registerTask(() -> {
            try {
                //注册serverChannel到selector
                serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            }
        });
    }
}
