package com.example.study.test.buffer;

import com.example.study.test.ReaderUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FileBufferReader {

    private FileInputStream fileIn;
    private ByteBuffer byteBuf;
    private long fileLength;
    private int arraySize;
    private byte[] array;

    private Deque deque = new ConcurrentLinkedDeque();
    private Queue queue = new ArrayBlockingQueue(10);
//    private ExecutorService executorService;
    private ThreadPoolExecutor executorService;


    public FileBufferReader(String fileName, int arraySize,int threadSize) throws IOException {
        this.fileIn = new FileInputStream(fileName);
        this.fileLength = fileIn.getChannel().size();
        this.arraySize = arraySize;
        this.byteBuf = ByteBuffer.allocate(arraySize);
//        executorService = Executors.newFixedThreadPool(threadSize);
        executorService =
                new ThreadPoolExecutor(threadSize,threadSize,10000,TimeUnit.SECONDS,new LinkedBlockingQueue(200));
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
//                System.out.println("=====" + deque.size());
                executorService.execute(new AnalysisDataTask((byte[]) deque.pop()));
            }

            return bytes;
        }else {
            executorService.shutdown();
            System.out.println("======");
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

        FileBufferReader reader = new FileBufferReader("/Users/mac/Desktop/技术大赛/20g.log", 1024 * 1024*100,16);
        while (reader.read() != -1){
//            Thread.sleep(10);

        }

        AnalysisDataTask analysisDataTask = new AnalysisDataTask();
        Map<Bytes, AtomicInteger> stMap = analysisDataTask.getStMap();

        CompareUtil.sourMap(stMap);


//        for(Map.Entry<Bytes, AtomicInteger> entry:stMap.entrySet()){
//            System.out.println(entry.getKey() + " : " + entry.getValue());
//        }


//        reader.close();
        Long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.printf("stream Diff: %d ms\n", estimatedTime);
    }
}
