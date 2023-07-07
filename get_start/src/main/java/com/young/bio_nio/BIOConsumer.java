package com.young.bio_nio;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;


/**
 * @A-描述 :
 * * -
 * * -
 * @A-作者 young
 * @E-邮箱 PlutoYcr520@outlook.com
 * @T-时间 2023-07-05 14:09
 */
public class BIOConsumer {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 8080);
        while (true) {
            try {
                socket.getOutputStream().write((new Date() + ": hello, world").getBytes());
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}