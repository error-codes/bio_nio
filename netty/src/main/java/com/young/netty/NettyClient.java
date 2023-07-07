package com.young.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @A-描述 :
 * * -
 * * -
 * @A-作者 young
 * @E-邮箱 PlutoYcr520@outlook.com
 * @T-时间 2023-07-05 14:09
 */
public class NettyClient {

    private static final Integer MAX_RETRY = 5;

    public static void main(String[] args) {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();

        bootstrap
                // 1. 指定线程模型
                .group(workerGroup)
                // 2. 指定IO类型为 NIO
                .channel(NioSocketChannel.class)
                // 3. IO处理逻辑
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new FirstClientHandler());
                    }
                });

        // 4. 建立连接
        connect(bootstrap, "localhost", 8080, MAX_RETRY);
    }


    /**
     * 自动重连
     *
     * @param bootstrap
     * @param host
     * @param port
     */
    private static void connect(Bootstrap bootstrap, String host, int port, int retry) {
        bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("连接成功!");
            } else if (retry == 0) {
                System.out.println("重试失败，放弃连接!");
            } else {
                // 第几次重连
                int order = (MAX_RETRY - retry) + 1;
                // 本次时间间隔
                int delay = 1 << order;
                System.out.println(new Date() + ": 连接失败，第" + order + "次重连......");
                bootstrap.config().group().schedule(() -> connect(bootstrap, host, port, retry - order), delay, TimeUnit.SECONDS);
            }
        });
    }

    /**
     * attr()方法可以为客户端Channel也就是NioSocketChannel绑定自定义属性，然后通过channel.attr()方法取出这个属性
     *
     * @param bootstrap
     */
    private static void attr(final Bootstrap bootstrap) {
        bootstrap.attr(AttributeKey.newInstance("clientName"), "nettyClient");
    }

    /**
     * option()方法可以为连接设置一些TCP底层相关的属性
     *
     * @param bootstrap
     */
    private static void option(final Bootstrap bootstrap) {
        bootstrap
                // 连接的超时时间
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                // 是否开启TCP底层心跳机制
                .option(ChannelOption.SO_KEEPALIVE, true)
                // 是否开启Nagle算法，高实时性true，减少网络交互false
                .option(ChannelOption.TCP_NODELAY, true);
    }
}
