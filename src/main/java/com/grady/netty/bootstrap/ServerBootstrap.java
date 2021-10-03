package com.grady.netty.bootstrap;

import com.grady.netty.server.Boss;
import com.grady.netty.server.NioServerSocketChannelFactory;

import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;

public class ServerBootstrap {

    private NioServerSocketChannelFactory factory;

    public ServerBootstrap(NioServerSocketChannelFactory factory) {
        this.factory = factory;
    }

    public void bind(final SocketAddress localAddress){
        try {
            // 获得一个ServerSocket通道
            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            // 设置通道为非阻塞
            serverChannel.configureBlocking(false);
            // 将该通道对应的ServerSocket绑定到port端口
            serverChannel.socket().bind(localAddress);
            Boss nextBoss = factory.nextBoss();
            //向boss注册一个ServerSocketChannel
            nextBoss.registerAcceptChannelTask(serverChannel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
