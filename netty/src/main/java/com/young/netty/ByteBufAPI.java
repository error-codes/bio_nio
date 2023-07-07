package com.young.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.EmptyByteBuf;

/**
 * @A-描述 :
 * * -
 * * -
 * @A-作者 young
 * @E-邮箱 PlutoYcr520@outlook.com
 * @T-时间 2023-07-05 17:03
 */
public class ByteBufAPI {

    public static void main(String[] args) {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();

        //////////////////////////////////////////////////////
        //                      容量API                      //
        //////////////////////////////////////////////////////

        // capacity() 方法表示 ByteBuf 底层占用了多少字节的内存（包括丢弃字节、可读字节、可写字节）
        // - - 不同的底层实现机制有不同的计算方式
        byteBuf.capacity();

        // maxCapacity() 方法表示 ByteBuf 底层最大能够占用多少字节的内存
        // - - 当向 ByteBuf 中写数据时，如果发现容量不足，则进行扩容，知道扩容到 maxCapacity，超过则抛出异常。
        byteBuf.maxCapacity();

        // readableBytes() 方法表示 ByteBuf 当前可读的字节数，它的值等于 writeIndex - readerIndex
        byteBuf.readableBytes();

        // isReadable() 方法表示 ByteBuf 是否可读，如果 writerIndex - readerIndex 等于 0，则不可读返回 false，反之，返回 true
        byteBuf.isReadable();

        // writableBytes() 方法表示 ByteBuf 当前可写的字节数，它的值等于 capacity - writerIndex
        byteBuf.writableBytes();

        // isWritable() 方法表示 ByteBuf 是否可写，如果 capacity - writerIndex 等于 0，则不可写返回 false，反正，返回 true
        byteBuf.isWritable();

        // maxWritableBytes() 方法表示可写的最大字节数，它的值等于 maxCapacityWriterIndex
        byteBuf.maxWritableBytes();


        /////////////////////////////////////////////////////
        //                   读写指针的API                   //
        /////////////////////////////////////////////////////

        // readerIndex() 方法表示当前的读指针 readerIndex
        byteBuf.readerIndex();

        // readerIndex(int) 方法表示设置读指针 readerIndex
        byteBuf.readerIndex(10);

        // writeIndex() 方法表示当前的写指针 writerIndex
        byteBuf.writerIndex();

        // writeIndex(int) 方法表示设置写指针 writerIndex
        byteBuf.writerIndex(10);

        // markReaderIndex() 方法表示标记当前的读指针 readerIndex
        byteBuf.markReaderIndex();

        // resetReaderIndex() 方法表示恢复读指针到之前的标记点
        byteBuf.resetReaderIndex();

        // markWriterIndex() 方法表示标记当前的写指针 writerIndex
        byteBuf.markWriterIndex();

        // resetWriterIndex() 方法表示恢复写指针到之前的标记点
        byteBuf.resetWriterIndex();


        //////////////////////////////////////////////////////
        //                      读写API                      //
        //////////////////////////////////////////////////////

        // writeBytes(byte[] src) 方法表示把字节数组 src 里的数据全部写到 ByteBuf
        byteBuf.writeBytes(new byte[10]);

        // readBytes(byte[] dst) 方法表示把 ByteBuf 里的数据全部读取到 dst 数组
        byteBuf.readBytes(new byte[10]);

        // writeByte(int num) 方法表示往 ByteBuf 的指定索引 num 写入一个字节
        // - - 类似的还有writeBoolean()、writeChar()、writeShort()、writeInt()、writeLong()、writeFloat()、writeDouble()
        byteBuf.writeByte(10);

        // readByte() 表示从 ByteBuf 中读取一个字节
        // - - 类似的还有readBoolean()、readChar()、readShort()、readInt()、readLong()、readFloat()、readDouble()
        byteBuf.readByte();

        // setBytes(int num, byte[] src) 方法表示往 ByteBuf 写入字节数组 src，从指定的 num 索引开始写入
        byteBuf.setBytes(1, new byte[10]);

        // setByte(int num, byte src) 方法表示往ByteBuf 的指定索引 num 中写入一个字节数组 src
        byteBuf.setByte(1, 10);

        // getBytes(int num, byte[] dst) 方法表示从ByteBuf 的指定索引 num 开始将读取到的字节数据存储到 dst 数组
        byteBuf.getBytes(1, new byte[10]);

        // getByte(int num) 方法表示从 ByteBuf 的指定索引 num 处读取一个字节
        byteBuf.getByte(1);

        /********************************************************************************************************************************************
         * getBytes()、getByte()、setBytes()、setByte() 与 readBytes()、readByte()、writeBytes()、writeByte()的区别是前者不会改变读写指针，而后者会改变读写指针 *
         ********************************************************************************************************************************************/

        // release() 方法表示将 ByteBuf 的引用计数减 1，并在引用计数达到 0 时释放相关资源
        byteBuf.release();

        // retain() 方法表示将当前 ByteBuf 的引用计数加 1，防止在并发操作中释放该 ByteBuf
        byteBuf.retain();

        // slice() 方法表示从原始 ByteBuf 中截取一段，这段数据是从 readerIndex 到 writeIndex 的，同时返回的新的 ByteBuf 的最大容量 maxCapacity 为原始 ByteBuf 的 readableBytes()
        byteBuf.slice();

        // duplicate() 方法表示把整个 ByteBuf 都截取出来，包括所有的数据、指针信息
        byteBuf.duplicate();

        // copy() 方法表示创建当前 ByteBuf 的一个完全独立的副本，包括数据和内部状态。
        byteBuf.copy();

        // retainedSlice() 方法表示截取内存片段的同时，并增加内存引用计数，等价于 slice().retain()
        byteBuf.retainedSlice();

        // retainedDuplicate() 方法表示截取整个内存片段的同时，并增加内存计数引用，等价于 duplicate().retain()
        byteBuf.retainedDuplicate();

        /***********************************************************************************************************************************
         * 1. slice() 方法与 duplicate() 方法的相同点是:                                                                                       *
         * * - - 底层内存及引用计数与原始 ByteBuf 共享也就是说，经过 slice() 方法或者 duplicate() 方法返回的 ByteBuf 调用 write 系列方法，               *
         * * - - 都会影响到原始 ByteBuf，但是它们都维持着与原始 ByteBuf 相同的内存引用计数和不同的读写指针。                                             *
         ***********************************************************************************************************************************
         * 2. slice() 方法与 duplicate() 方法的不同点是:                                                                                       *
         * * - - slice() 方法只截取从 readerIndex 到 writerIndex 之间的数据，它返回的 ByteBuf 的最大容量被限制到原 ByteBuf 的 readableBytes()，       *
         * * - - 而 duplicate() 方法是把整个 BteBuf 都与原 ByteBuf 共享。                                                                       *
         ***********************************************************************************************************************************
         * 3. slice() 方法与 duplicate() 方法不会复制数据，它们只是通过改变读写指针来改变读写的行为，而最后一个方法 copy() 会直接从原 ByteBuf 中复制所有信息， *
         * * - - 包括读写指针及底层对应的数据，因此，往 copy() 方法返回的 ByteBuf 中写数据不会影响原 ByteBuf。                                          *
         ***********************************************************************************************************************************
         * 4. slice() 方法和 duplicate() 方法不会改变 ByteBuf 的引用计数，所以原始 ByteBuf 调用 release() 方法后发现引用计数为零，便立即开始释放内存，     *
         * * - - 调用这两个方法返回的 ByteBuf 也会被释放。这时候如果再对它们进行读写，就会报错。因此，我们可以通过调用一次 retain() 方法来增加引用，            *
         * * - - 表示它们对应的底层内存多了一次引用，引用计数为 2。在释放内存时，需要调用两次 release() 方法，将引用计数降到零，才会释放内存。                  *
         ***********************************************************************************************************************************
         * 5. 这三个方法均维护着自己的读写指针，与原始 ByteBuf 读写指针无关，互不影响。                                                                *
         ***********************************************************************************************************************************/


    }
}
