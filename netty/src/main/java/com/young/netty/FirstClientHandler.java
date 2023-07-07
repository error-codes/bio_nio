package com.young.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Date;

/**
 * @A-描述 :
 * * - 1. 这个逻辑处理器继承自ChannelInboundHandlerAdapter，重写了channelActive()方法，这个方法会在客户端连接建立成功之后被调用。
 * * - 2. 客户端连接建立成功后，调用channelActive()方法。在这里编写向服务端写数据的逻辑。
 * * - 3. 写数据的逻辑分为三步：首先需要获取一个Netty对二进制数据的抽象ByteBuf。
 * * - - - - - - - - - - -  ctx.alloc()获取到一个ByteBuf的内存管理器，其作用就是分配一个ByteBuf。
 * * - - - - - - - - - - -  然后把字符串的二进制数据填充到ByteBuf，这样就获取到Netty需要的数据格式。
 * * - - - - - - - - - - -  最后调用ctx.channel().writeAndFlush()把数据写到服务端。
 * * -
 * @A-作者 young
 * @E-邮箱 PlutoYcr520@outlook.com
 * @T-时间 2023-07-05 14:55
 */
public class FirstClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(new Date() + ": 客户端写出数据");

        // 1. 获取数据
        ByteBuf byteBuf = getByteBuf(ctx);
        // 2. 写数据
        ctx.channel().writeAndFlush(byteBuf);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println(new Date() + ": 客户端读到数据 -> " + byteBuf.toString(Charset.defaultCharset()));
    }

    private static ByteBuf getByteBuf(ChannelHandlerContext ctx) {
        // 1. 获取二进制抽象 ByteBuf
        ByteBuf byteBuf = ctx.alloc().buffer();

        // 2. 准备数据，执行字符串的字符集为 UTF-8
        byte[] bytes = "你好，Young!".getBytes(Charset.defaultCharset());

        // 3. 准备数据填充到 ByteBuf
        byteBuf.writeBytes(bytes);
        return byteBuf;
    }
}
