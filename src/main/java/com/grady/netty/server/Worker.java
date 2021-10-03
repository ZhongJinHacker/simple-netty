package com.grady.netty.server;

import java.nio.channels.SocketChannel;

public interface Worker {

    void registerNewChannelTask(SocketChannel channel);
}
