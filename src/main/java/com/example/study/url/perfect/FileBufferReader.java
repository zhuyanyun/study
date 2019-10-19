package com.example.study.url.perfect;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class FileBufferReader {

    private FileInputStream fileIn;
    private ByteBuffer byteBuf;
    private long fileLength;
    private int arraySize;
    private byte[] array;

    private Deque deque = new ConcurrentLinkedDeque();
//    private Queue queue = new ArrayBlockingQueue(10);
    private static ThreadPoolExecutor executorService;
    private static AnalysisDataTask analysisDataTask = new AnalysisDataTask();


    public FileBufferReader(String fileName, int arraySize,int threadSize) throws IOException {
        this.fileIn = new FileInputStream(fileName);
        this.fileLength = fileIn.getChannel().size();
        this.arraySize = arraySize;
        this.byteBuf = ByteBuffer.allocate(arraySize);
        executorService =
                new ThreadPoolExecutor(threadSize,threadSize,10000,TimeUnit.SECONDS,new LinkedBlockingQueue(Integer.MAX_VALUE));
    }

    public int read() throws IOException {
        FileChannel fileChannel = fileIn.getChannel();
        int bytes = fileChannel.read(byteBuf);// 读取到ByteBuffer中

        int i = 0;
        if (bytes != -1) {
            array = new byte[bytes];// 字节数组长度为已读取长度
            byteBuf.flip();
            byteBuf.get(array);
            deque.offer(array);
            byteBuf.clear();

            if(!deque.isEmpty()){   //队列满了不会报错
                if(i == 0) {
                    executorService.execute(analysisDataTask.new AnalysisDataRunnable((byte[]) deque.pop()));
                }
            }

            return bytes;
        }else {
            executorService.shutdown();
        }
        return -1;
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

    public static void main(String[] args) throws IOException, InterruptedException {

        Long startTime = System.currentTimeMillis();

        //读取快，处理慢，需要平衡处理，随意读取的要小，线程要少
//        FileBufferReader reader = new FileBufferReader("/Users/mac/Desktop/技术大赛/access_20190926.log", 1024 * 800*1,4);
        FileBufferReader reader = new FileBufferReader("../access.log", 1024 * 800*1,4);
        while (reader.read() != -1){
        }


        for(;;){
            if(executorService.isTerminated()){
                break;
            }
        }
        Long endTime = System.currentTimeMillis() - startTime;
        System.out.printf("stream Diff: %d ms\n", endTime);




        Map<Bytes, AtomicInteger> stMap = analysisDataTask.getStMap();
        Map<Bytes, AtomicInteger> apiMap = analysisDataTask.getApiMap();

        CompareUtil.sourMap(stMap,apiMap);


        reader.close();
        Long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.printf("stream Diff: %d ms\n", estimatedTime);
    }
}
