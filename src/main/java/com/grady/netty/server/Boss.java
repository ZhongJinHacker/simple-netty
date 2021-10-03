package com.grady.netty.server;

import java.nio.channels.ServerSocketChannel;

public interface Boss {


    /**
     * 加入一个新的ServerSocket
     * @param serverChannel
     */
    void registerAcceptChannelTask(ServerSocketChannel serverChannel);
}
