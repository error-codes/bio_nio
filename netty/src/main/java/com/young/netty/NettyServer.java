package com.young.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * @A-描述 :
 * * -
 * * -
 * @A-作者 young
 * @E-邮箱 PlutoYcr520@outlook.com
 * @T-时间 2023-07-05 14:09
 */
public class NettyServer {

    public static void main(String[] args) {
        // 首先创建了两个NioEventLoopGroup，这两个对象可以看作传统IO编程模型的两大线程组。
        // 1. bossGroup表示监听端口，接收新连接的线程组;
        // 2. workerGroup表示处理每一个连接的数据读写的线程组。
        //
        // 用生活中的例子来讲就是：
        // 一个工厂要运作，必然要有一个老板负责从外面接活，然后有很多员工，负责具体干活。
        // 老板就是bossGroup，员工们就是workerGroup，bossGroup接收完连接，交给workerGroup去处理。
        //
        // 其次创建了一个引导类ServerBootstrap，这个类将引导服务端的启动工作。
        // 通过group(bossGroup,workerGroup)给引导类配置两大线程组，这个引导类的线程模型也就定型了。
        //
        // 然后指定服务端的IO模型为NIO，上述代码通过 .channel(NioServerSocketChannel.class)来指定IO模型,也可以有其他选择。
        // 如果你想指定IO模型为BIO，那么这里配置上OioServerSocketChannel.class类型即可。当然通常我们也不会这么做，因为Netty的优势就在于NIO。
        // 接着调用childHandler()方法，给这个引导类创建一个ChannelInitializer，主要是定义后续每个连接的数据读写，对于业务处理逻辑，不理解也没关系，后面我们会详细分析。
        // 在ChannelInitializer这个类中，有一个泛型参数NioSocketChannel，这个类就是Netty对NIO类型连接的抽象，而前面的NioServerSocketChannel也是对NIO类型连接的抽象，
        // NioServerSocketChannel和NioSocketChannel的概念可以与BIO编程模型中的ServerSocket和Socket两个概念对应。
        //
        // 最小化参数配置到这里就完成了，总结一下就是，要启动一个Netty服务端，必须要指定三类属性：
        // 1. 线程模型
        // 2. IO模型
        // 3. 连接读写处理逻辑
        //
        // 有了这三者，之后再调用bind(8080)，就可以在本地绑定一个8080端口启动服务端。
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) {
                        nioSocketChannel.pipeline().addLast(new FirstServerHandler());
                    }
                });

        bind(serverBootstrap, 8080);
    }

    private static void bind(final ServerBootstrap serverBootstrap, final int port) {
        serverBootstrap.bind(port).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("端口 [" + port + "] 绑定成功!");
            } else {
                System.out.println("端口 [" + port + "] 绑定失败!");
            }
        });
    }

    /**
     * handler()方法可以指定在服务端启动过程中的一些逻辑，通常情况用不到该方法
     *
     * @param serverBootstrap
     */
    private static void handler(final ServerBootstrap serverBootstrap) {
        serverBootstrap.handler(new ChannelInitializer<NioServerSocketChannel>() {
            @Override
            protected void initChannel(NioServerSocketChannel nioServerSocketChannel) throws Exception {
                System.out.println("服务端启动中");
            }
        });
    }


    /**
     * attr()方法可以给服务端Channel，也就是NioServerSocketChannel指定一些自定义属性，然后通过channel.attr()取出这个属性。
     *
     * @param serverBootstrap
     */
    private static void attr(final ServerBootstrap serverBootstrap) {
        serverBootstrap.attr(AttributeKey.newInstance("serverName"), "nettyServer");
    }


    /**
     * childAttr()方法可以给每一个连接指定自定义属性，后续可以通过attr()方法取出该属性
     *
     * @param serverBootstrap
     */
    private static void childAttr(final ServerBootstrap serverBootstrap) {
        serverBootstrap.childAttr(AttributeKey.newInstance("clientKey"), "clientValue");
    }

    /**
     * option()方法可以给服务端channel设置一些TCP参数
     *
     * @param serverBootstrap
     */
    private static void option(final ServerBootstrap serverBootstrap) {
        serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
    }

    /**
     * childOption()方法可以给每一个连接都设置一些TCP参数
     *
     * @param serverBootstrap
     */
    private static void childOption(final ServerBootstrap serverBootstrap) {
        serverBootstrap
                // 是否开启TCP底层心跳机制
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                // 是否开启Nagle算法，高实时性true，减少网络交互false
                .childOption(ChannelOption.TCP_NODELAY, true);
    }
}
