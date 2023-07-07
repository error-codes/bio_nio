package com.young.bio_nio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @A-描述 :
 * * - 在上面的示例中，从服务端代码可以看到，在传统的IO模型中，每个连接创建成功之后都需要由一个线程来维护，
 * * - 每个线程都包含一个while死循环，那么1万个连接对应1万个线程，继而有1万个while死循环，这就带来如下几个问题。
 * * - 1.线程资源受限:线程是操作系统中非常宝贵的资源，同一时刻有大量的线程处于阻塞状态，是非常严重的资源浪费，操作系统耗不起。
 * * - 2.线程切换效率低下:单机CPU核数固定，线程爆炸之后操作系统频繁进行线程切换，应用性能急剧下降。
 * * - 3.除了以上两个问题，在IO编程中，我们看到数据读写是以字节流为单位的。
 * * -
 * @A-作者 young
 * @E-邮箱 PlutoYcr520@outlook.com
 * @T-时间 2023-07-05 14:09
 */
public class BIOServer {

    public static void main(String[] args) throws IOException {
        // 当我们的服务器接收到一个连接后，并且没有接收到客户端发送的数据时，是会阻塞在read()方法中的
        // 那么此时如果再来一个客户端的请求，服务端是无法进行响应的。
        // 换言之：在不考虑多线程的情况下，BIO是无法处理多个客户端请求的。
        singleBio();

        // ------------------------------------------------------

        // 多线程BIO服务器虽然解决了单线程BIO无法处理并发的弱点，但是也带来一个问题：
        // 在传统的IO模型中，每个连接创建成功之后都需要由一个线程来维护。
        // 每个线程都包含一个while死循环，那么1万个连接对应1万个线程，继而有1万个while死循环，这就带来如下几个问题。
        // 1.线程资源受限:线程是操作系统中非常宝贵的资源，同一时刻有大量的线程处于阻塞状态，是非常严重的资源浪费，操作系统耗不起。
        // 2.线程切换效率低下:单机CPU核数固定，线程爆炸之后操作系统频繁进行线程切换，应用性能急剧下降。
        // 3.除了以上两个问题，在IO编程中，我们看到数据读写是以字节流为单位的。
        multiBio();
    }


    /**
     * 在每一个连接请求到来时，创建一个线程去执行这个连接请求，就可以在BIO中处理多个客户端请求了
     * 这也就是为什么BIO的其中一条概念是服务器实现模式为一个连接一个线程
     * 即客户端有连接请求时服务器端就需要启动一个线程进行处理。
     */
    public static void multiBio() throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);

        new Thread(() -> {
            while (true) {
                try {
                    // ①阻塞方法获取新连接
                    Socket socket = serverSocket.accept();
                    // ②为每一个新连接都创建一个新线程，负责读取数据
                    new Thread(() -> {
                        try {
                            int len;
                            byte[] data = new byte[1024];
                            InputStream inputStream = socket.getInputStream();
                            // ③按字节流方式读取数据
                            while ((len = inputStream.read(data)) != -1) {
                                System.out.println(new String(data, 0, len));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 从运行结果中我们可以看到，服务器端在启动后：
     * <p>
     * 1）首先需要等待客户端的连接请求（第一次阻塞）；
     * 2）如果没有客户端连接，服务端将一直阻塞等待；
     * 3）然后当客户端连接后，服务器会等待客户端发送数据（第二次阻塞）；
     * 4）如果客户端没有发送数据，那么服务端将会一直阻塞等待客户端发送数据。
     * <p>
     * 服务端从启动到收到客户端数据的这个过程，将会有两次阻塞的过程：
     * <p>
     * 1）第一次在等待连接时阻塞；
     * 2）第二次在等待数据时阻塞。
     * <p>
     * BIO会产生两次阻塞，这就是BIO的非常重要的一个特点。
     */
    public static void singleBio() throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);

        // ①阻塞方法获取新连接
        Socket socket = serverSocket.accept();
        // ②为每一个新连接都创建一个新线程，负责读取数据

        try {
            int len;
            byte[] data = new byte[1024];
            InputStream inputStream = socket.getInputStream();
            // ③按字节流方式读取数据
            while ((len = inputStream.read(data)) != -1) {
                System.out.println(new String(data, 0, len));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
