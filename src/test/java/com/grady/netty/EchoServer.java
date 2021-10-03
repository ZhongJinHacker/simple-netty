package com.grady.netty;

import com.grady.netty.bootstrap.ServerBootstrap;
import com.grady.netty.server.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EchoServer {

    public static void main(String[] args) {
        ExecutorService bossExecutor = Executors.newCachedThreadPool();
        ExecutorService workExecutor = Executors.newSingleThreadExecutor();
        NioServerSocketChannelFactory factory = new NioServerSocketChannelFactory(bossExecutor, workExecutor);
        ServerBootstrap serverBootstrap = new ServerBootstrap(factory);
        serverBootstrap.bind(new InetSocketAddress(8080));
        System.out.println("start");
    }
}
