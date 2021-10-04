package com.grady.netty;

import com.grady.netty.bootstrap.ServerBootstrap;
import com.grady.netty.server.NioEventLoopGroup;

import java.net.InetSocketAddress;

public class EchoServer {

    public static void main(String[] args) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup);
        serverBootstrap.bind(new InetSocketAddress(8080));
        System.out.println("start");
    }
}
