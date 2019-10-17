package com.example.study.test.buffer;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileBufferReader {

    private FileInputStream fileIn;
    private ByteBuffer byteBuf;
    private long fileLength;
    private int arraySize;
    private byte[] array;

    private Deque deque = new ConcurrentLinkedDeque();
    private Queue queue = new ArrayBlockingQueue(10);
    private ExecutorService executorService;



    public FileBufferReader(String fileName, int arraySize,int threadSize) throws IOException {
        this.fileIn = new FileInputStream(fileName);
        this.fileLength = fileIn.getChannel().size();
        this.arraySize = arraySize;
        this.byteBuf = ByteBuffer.allocate(arraySize);
        executorService = Executors.newFixedThreadPool(threadSize);
    }

    public void read() throws IOException {
        FileChannel fileChannel = fileIn.getChannel();
        int bytes = fileChannel.read(byteBuf);// 读取到ByteBuffer中

        int i = 0;
        for(;;){
            if (bytes != -1) {
                array = new byte[bytes];// 字节数组长度为已读取长度
                byteBuf.flip();
                byteBuf.clear();
                deque.offer(array);

                if(!deque.isEmpty()){   //队列满了不会报错
                    executorService.execute(new AnalysisDataTask((byte[]) deque.pop()));
                }
            }else {
                System.out.println("======");
                break;
            }
        }

        executorService.shutdown();

    }

    public void close() throws IOException {
        fileIn.close();
        array = null;
    }

    public byte[] getArray() {
        return array;
    }

    public long getFileLength() {
        return fileLength;
    }

    public static void main(String[] args) throws IOException {

        Long startTime = System.currentTimeMillis();

        FileBufferReader reader = new FileBufferReader("/Users/mac/Desktop/技术大赛/access_20190926.log", 1024,10);
        reader.read();
        reader.close();
        Long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.printf("stream Diff: %d ms\n", estimatedTime);
    }
}
