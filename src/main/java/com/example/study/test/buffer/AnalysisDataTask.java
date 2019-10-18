package com.example.study.test.buffer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AnalysisDataTask {


    private static Map<Bytes,AtomicInteger> stMap = new ConcurrentHashMap<Bytes,AtomicInteger>();
    private static Map<Bytes,AtomicInteger> apiMap = new ConcurrentHashMap<Bytes,AtomicInteger>();

    public  Map<Bytes, AtomicInteger> getStMap() {
        return stMap;
    }

    public static void setStMap(Map<Bytes, AtomicInteger> stMap) {
        AnalysisDataTask.stMap = stMap;
    }

    public static Map<Bytes, AtomicInteger> getApiMap() {
        return apiMap;
    }

    public static void setApiMap(Map<Bytes, AtomicInteger> apiMap) {
        AnalysisDataTask.apiMap = apiMap;
    }


    //第一次自己写内部类
    class AnalysisDataRunnable implements Runnable{

        private byte[] buff;

        public AnalysisDataRunnable(byte[] deque){
            buff = deque;
        }


        @Override
        public void run() {
            int index = 0;
            for (;;) {
                if(buff ==null){
                    break;
                }

                try {
                    // 跳过前三个table
                    for (;;) {
                        byte b = buff[index++];
                        if (b == '\t') {
                            break;
                        }
                    }

                    for (;;) {
                        byte b = buff[index++];
                        if (b == '\t') {
                            break;
                        }
                    }

                    for (;;) {
                        byte b = buff[index++];
                        if (b == '\t') {
                            break;
                        }
                    }

                    int domainStart = index;
                    int apiStart = 0;
                    int spCn = 0;

                    // 读取内容
                    for (;;) {                //截取	\t之间的有用
                        byte b = buff[index++];
                        if (b == '\t') {
                            break;
                        }

                        if (b == 32) {      //空格
                            spCn++;
                            if (spCn == 1) {      //域名
                                int len = index - 1 - domainStart;
                                byte[] domainBuff = new byte[len];
                                System.arraycopy(buff, domainStart, domainBuff, 0, len);
                                Bytes key = new Bytes(domainBuff);
                                AtomicInteger domainInteger = stMap.computeIfAbsent(key, k -> new AtomicInteger(0));
                                domainInteger.incrementAndGet();
                            } else if (spCn == 5) {
                                apiStart = index;
                            } else if (spCn == 6) {
                                int len = index - 1 - apiStart;
                                byte[] apiBuff = new byte[len];
                                System.arraycopy(buff, apiStart, apiBuff, 0, len);
                                Bytes key = new Bytes(apiBuff);
                                AtomicInteger domainInteger = apiMap.computeIfAbsent(key, k -> new AtomicInteger(0));
                                domainInteger.incrementAndGet();
                                break;
                            }
                        }

                        if (spCn == 5 && b == 63) {     //'?'
                            int len = index - 1 - apiStart;
                            byte[] apiBuff = new byte[len];
                            System.arraycopy(buff, apiStart, apiBuff, 0, len);
                            Bytes key = new Bytes(apiBuff);
                            AtomicInteger domainInteger = apiMap.computeIfAbsent(key, k -> new AtomicInteger(0));
                            domainInteger.incrementAndGet();
                            break;
                        }
                    }

                    // 读到行尾部
                    for (;;) {
                        byte b = buff[index++];
                        if (b == '\n') {
                            break;
                        }
                    }

                    // 跳过有效内容
//                if (index >= byteBuffer.position()) {
//                    break;
//                }
                } catch (Exception e) {
                    break;
                }
            }

        }
    }
}
