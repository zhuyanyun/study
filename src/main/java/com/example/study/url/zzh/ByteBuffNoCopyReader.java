package com.example.study.url.zzh;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ByteBuffNoCopyReader {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        try {
            String outputPath = "/Users/mac/Desktop/st/";
            String name = "zzh";
            String domainFileName = name + "_domain_my";
            String urlFileName = name + "_uri_my";
            int poolSize = 6;

            // 线程池以及统计map
            LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
            ConcurrentHashMap<ByteBuffKey, AtomicInteger> domainMap = new ConcurrentHashMap<>();
            ConcurrentHashMap<ByteBuffKey, AtomicInteger> apiMap = new ConcurrentHashMap<>();
            ThreadPoolExecutor pool = new ThreadPoolExecutor(poolSize, poolSize, 2, TimeUnit.SECONDS, workQueue, new CustomThreadFactory());

            // 批次大小
            int bz = 1024 * 512;

            // 剩余字节缓存大小
            int leftBuffSize = 1024 * 8;
            // 缓存大小
            int buffSize = bz + leftBuffSize;

            // 缓存
            int cacheSize = 64;
            LinkedBlockingQueue<EmptyNode> emptyQueue = new LinkedBlockingQueue<>(cacheSize);
            for (int i = 0; i < cacheSize; i++) {
                ByteBuffer buff = ByteBuffer.allocate(buffSize);
                ByteBuffKey domainKey = new ByteBuffKey(new byte[0], 0, 0);
                ByteBuffKey apiKey = new ByteBuffKey(new byte[0], 0, 0);
                EmptyNode emptyNode = new EmptyNode(domainKey, apiKey, buff);
                emptyQueue.offer(emptyNode);
            }

            // 打开流
            FileInputStream s = new FileInputStream("/Users/mac/Desktop/st/access_all.log");
            BufferedInputStream fis = new BufferedInputStream(s, bz);

            // 上次剩余
            ByteBuffer left = ByteBuffer.allocate(leftBuffSize);
            for (;;) {
                EmptyNode emptyNode = emptyQueue.take();
                ByteBuffer byteBuff = emptyNode.getBuff();
                byte[] buff = byteBuff.array();
                int leftLen = left.position();

                if (leftLen > 0) {
                    byteBuff.put(left.array(), 0, leftLen);
                }

                int len = fis.read(buff, leftLen, bz);

                int index = len + leftLen - 1;
                try {
                    for (;;) {
                        if (buff[index] == '\n') {
                            byteBuff.position(index);
                            pool.execute(new ByteBuffNoCopyReaderTask(emptyNode, domainMap, apiMap, emptyQueue));
                            left.clear();

                            // 拷贝剩余字符到字节组
                            leftLen = len + leftLen - index - 1;
                            if (leftLen != 0) {
                                left.put(buff, index + 1, leftLen);
                            }
                            break;
                        }
                        index--;
                    }
                } catch (Exception e) {
                    // 用异常结束
                }
                if (len < bz) {
                    break;
                }
            }

            pool.shutdown();
            while (!pool.isTerminated()) {
                Thread.sleep(1);
            }

            ArrayList<Map.Entry<ByteBuffKey, AtomicInteger>> results = sortMap(domainMap);
            ByteBuffCSVUtil.createCSVFile(null, results, outputPath, domainFileName, 20);
            results = sortMap(apiMap);
            ByteBuffCSVUtil.createCSVFile(null, results, outputPath, urlFileName, 20);

            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("use time =: " + (System.currentTimeMillis() - startTime));
    }

    private static ArrayList<Map.Entry<ByteBuffKey, AtomicInteger>> sortMap(Map<ByteBuffKey, AtomicInteger> map) {
        ArrayList<Map.Entry<ByteBuffKey, AtomicInteger>> list = new ArrayList<>(map.entrySet());
        list.sort((e1, e2) -> {
            int one = e1.getValue().get();
            int two = e2.getValue().get();
            return -Integer.compare(one, two);
        });
        return list;
    }
}
