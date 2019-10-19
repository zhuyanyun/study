package com.example.study.url.zzh;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ByteBuffNoCopyReaderTask implements Runnable {

    private EmptyNode emptyNode;

    private ConcurrentHashMap<ByteBuffKey, AtomicInteger> stMap;

    private ConcurrentHashMap<ByteBuffKey, AtomicInteger> apiMap;

    private LinkedBlockingQueue<EmptyNode> emptyQueue;

    ByteBuffNoCopyReaderTask(EmptyNode emptyNode, ConcurrentHashMap<ByteBuffKey, AtomicInteger> stMap, ConcurrentHashMap<ByteBuffKey, AtomicInteger> apiMap,
            LinkedBlockingQueue<EmptyNode> emptyQueue) {
        this.emptyNode = emptyNode;
        this.stMap = stMap;
        this.apiMap = apiMap;
        this.emptyQueue = emptyQueue;
    }

    @Override
    public void run() {
        try {
            byte[] buff = emptyNode.getBuff().array();
            int index = 0;
            for (;;) {
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
                    for (;;) {
                        byte b = buff[index++];
                        if (b == '\t') {
                            break;
                        }

                        if (b == 32) {
                            spCn++;
                            if (spCn == 1) {
                                int len = index - 1 - domainStart;
                                ByteBuffKey key = emptyNode.getDomainKey().reset(buff, domainStart, len);
                                try {
                                    stMap.get(key).incrementAndGet();
                                } catch (Exception e) {
                                    AtomicInteger domainInteger = stMap.get(key);
                                    if (domainInteger == null) {
                                        domainInteger = new AtomicInteger(0);
                                        byte[] keyBuff = new byte[len];
                                        System.arraycopy(buff, domainStart, keyBuff, 0, len);
                                        key = new ByteBuffKey(keyBuff, 0, len);
                                        AtomicInteger putRs = stMap.putIfAbsent(key, domainInteger);
                                        if (putRs != null) {
                                            domainInteger = putRs;
                                        }
                                    }
                                    domainInteger.incrementAndGet();
                                }
                            } else if (spCn == 5) {
                                apiStart = index;
                            } else if (spCn == 6) {
                                int len = index - 1 - apiStart;
                                ByteBuffKey key = emptyNode.getApiKey().reset(buff, apiStart, len);
                                try {
                                    apiMap.get(key).incrementAndGet();
                                } catch (Exception e) {
                                    AtomicInteger domainInteger = apiMap.get(key);
                                    if (domainInteger == null) {
                                        domainInteger = new AtomicInteger(0);
                                        byte[] keyBuff = new byte[len];
                                        System.arraycopy(buff, apiStart, keyBuff, 0, len);
                                        key = new ByteBuffKey(keyBuff, 0, len);
                                        AtomicInteger putRs = apiMap.putIfAbsent(key, domainInteger);
                                        if (putRs != null) {
                                            domainInteger = putRs;
                                        }
                                    }
                                    domainInteger.incrementAndGet();
                                }
                                break;
                            }
                        }

                        if (spCn == 5 && b == 63) {
                            int len = index - 1 - apiStart;
                            ByteBuffKey key = emptyNode.getApiKey().reset(buff, apiStart, len);
                            try {
                                apiMap.get(key).incrementAndGet();
                            } catch (Exception e) {
                                AtomicInteger domainInteger = apiMap.get(key);
                                if (domainInteger == null) {
                                    domainInteger = new AtomicInteger(0);
                                    byte[] keyBuff = new byte[len];
                                    System.arraycopy(buff, apiStart, keyBuff, 0, len);
                                    key = new ByteBuffKey(keyBuff, 0, len);
                                    AtomicInteger putRs = apiMap.putIfAbsent(key, domainInteger);
                                    if (putRs != null) {
                                        domainInteger = putRs;
                                    }
                                }
                                domainInteger.incrementAndGet();
                            }
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
                    if (index >= emptyNode.getBuff().position()) {
                        break;
                    }
                } catch (Exception e) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 清楚标记，并重新加入空队列
            emptyNode.getBuff().clear();
            emptyQueue.offer(emptyNode);
        }
    }
}
