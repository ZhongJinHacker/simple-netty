package com.grady.netty.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;

public class NioServerWorker extends AbstractNioSelector implements Worker {


    public NioServerWorker(Executor workerExecutor, String threadName, NioServerSocketChannelFactory factory) {
        super(workerExecutor, threadName, factory);
    }

    @Override
    protected int select(Selector selector) throws IOException {
        return selector.select(500);
    }

    @Override
    protected void process(Selector selector) throws IOException {
        Set<SelectionKey> selectedKeys = selector.selectedKeys();
        if (selectedKeys.isEmpty()) {
            return;
        }
        for (Iterator<SelectionKey> ite = this.selector.selectedKeys().iterator(); ite.hasNext();){
            SelectionKey key = ite.next();
            ite.remove();
            SocketChannel channel = (SocketChannel) key.channel();

            // 数据总长度
            int bytesRead = 0;
            boolean failure = true;
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            //读取数据
            try {
                bytesRead = channel.read(buffer);
                failure = false;
            } catch (Exception e) {
                e.printStackTrace();
                failure = true;
            }
            //判断是否连接已断开
            if (bytesRead <= 0 || failure) {
                key.cancel();
                System.out.println("客户端断开连接");
            } else {
                System.out.println("收到数据:\n" + new String(buffer.array()));

                //回写数据
                String responseHeader = "HTTP/1.1 200"  + "\r\n" + "Content-Type：text/html" + "\r\n"
                        + "\r\n";
                String response  = responseHeader + "ok! ok!";
                System.out.println("响应数据：" + response);
                ByteBuffer outBuffer = ByteBuffer.wrap(response.getBytes());
                // 将消息回送给客户端
                channel.write(outBuffer);
                channel.close();
                key.cancel();
            }
        }
    }

    @Override
    public void registerNewChannelTask(SocketChannel channel) {
        System.out.println("工作线程启动");
        final Selector selector = this.selector;
        registerTask(() -> {
            try {
                channel.register(selector, SelectionKey.OP_READ);
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            }
        });
    }
}
