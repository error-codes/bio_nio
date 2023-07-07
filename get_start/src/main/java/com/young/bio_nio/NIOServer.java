package com.young.bio_nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * @A-描述 ：
 * * - 1.NIO模型中通常会有两个线程，每个线程都绑定一个轮询器Selector。这个例子中serverSelector负责轮询是否有新连接，clientSelector负责轮询连接是否有数据可读
 * * - 2.服务端监测到新连接之后，不再创建一个新线程，而是直接将新连接绑定到clientSelector上，这样就不用IO模型中的1万个while循环死等。
 * * - 3.clientSelector被一个while死循环包裹着，如果在某一时刻有多个连接有数据可读那么通过clientSelector.select(1)方法可以轮询出来，进而批量处理，参见
 * * - 4.数据的读写面向Buffer。
 * * - <p>
 * * - 其他细节部分，[因为实在是太复杂，所以笔者不再多讲，读者也不用对代码的细节深究到底。总之强烈不建议直接基于JDK原生NIO来进行网络开发，下面是笔者总结的原因。
 * * - 1.JDK的NIO编程需要了解很多概念，编程复杂，对NIO入门非常不友好，编程模型不友好，ByteBuffer的API简直“反人类”
 * * - 2.对NIO编程来说，一个比较合适的线程模型能充分发挥它的优势，而JDK没有实现需要自己实现，就连简单的自定义协议拆包都要自己实现。
 * * - 3.JDK的NIO底层由Epoll实现，该实现饱受诟病的空轮询Bug会导致CPU占用率飙升至100%。
 * * - 4.项目庞大之后，自行实现的NIO很容易出现各类Bug，维护成本较高，上面这些代码笔者都不能保证没有Bug。
 * * -
 * @A-作者 young
 * @E-邮箱 PlutoYcr520@outlook.com
 * @T-时间 2023-07-05 14:09
 */
public class NIOServer {

    public static void main(String[] args) throws IOException {
        Selector serverSelector = Selector.open();
        Selector clientSelector = Selector.open();

        new Thread(() -> {
            try {
                // 对应IO编程中的服务端启动
                ServerSocketChannel listenerChannel = ServerSocketChannel.open();
                listenerChannel.socket().bind(new InetSocketAddress(8080));
                listenerChannel.configureBlocking(false);
                listenerChannel.register(serverSelector, SelectionKey.OP_ACCEPT);

                while (true) {
                    // 检测是否有新连接，这里的1指阻塞的时间为1ms
                    if (serverSelector.select(1) > 0) {
                        Set<SelectionKey> set = serverSelector.selectedKeys();
                        Iterator<SelectionKey> keyIterator = set.iterator();
                        while (keyIterator.hasNext()) {
                            SelectionKey key = keyIterator.next();
                            if (key.isAcceptable()) {
                                try {
                                    // ①每来一个新连接，不需要创建一个线程，而是直接注册到 ClientSelector
                                    SocketChannel clientChannel = ((ServerSocketChannel) key.channel()).accept();
                                    clientChannel.configureBlocking(false);
                                    clientChannel.register(clientSelector, SelectionKey.OP_READ);
                                } finally {
                                    keyIterator.remove();
                                }
                            }
                        }
                    }
                }
            } catch (IOException ignored) {

            }
        }).start();

        new Thread(() -> {
            try {
                while (true) {
                    // ②批量轮询哪些连接有数据可读，这里的1指阻塞的时间为1ms
                    if (clientSelector.select(1) > 0) {
                        Set<SelectionKey> set = clientSelector.selectedKeys();
                        Iterator<SelectionKey> keyIterator = set.iterator();
                        while (keyIterator.hasNext()) {
                            SelectionKey key = keyIterator.next();
                            if (key.isReadable()) {
                                try {
                                    SocketChannel clientChannel = (SocketChannel) key.channel();
                                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                                    // ③面向Buffer
                                    clientChannel.read(byteBuffer);
                                    byteBuffer.flip();
                                    System.out.println(Charset.defaultCharset().newDecoder().decode(byteBuffer));
                                } finally {
                                    keyIterator.remove();
                                    key.interestOps(SelectionKey.OP_READ);
                                }
                            }
                        }
                    }
                }
            } catch (IOException ignored) {

            }
        }).start();
    }
}
