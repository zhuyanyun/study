package com.example.study.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ChannelFileReader {

//    private static final Logger logger = LoggerFactory.getLogger(ChannelFileReader.class);


    private FileInputStream fileIn;
    private ByteBuffer byteBuf;
    private long fileLength;
    private int arraySize;
    private byte[] array;


    private HashMap<String, Integer> map = new HashMap<>();
    public static BlockingQueue<String> queue = new ArrayBlockingQueue<String>(4);

    public ChannelFileReader(String fileName, int arraySize) throws IOException {
        this.fileIn = new FileInputStream(fileName);
        this.fileLength = fileIn.getChannel().size();
        this.arraySize = arraySize;
        this.byteBuf = ByteBuffer.allocate(arraySize);
    }

    public int read() throws IOException {
        FileChannel fileChannel = fileIn.getChannel();
        int bytes = fileChannel.read(byteBuf);// 读取到ByteBuffer中
        if (bytes != -1) {
            array = new byte[bytes];// 字节数组长度为已读取长度
            byteBuf.flip();
            byteBuf.get(array);// 从ByteBuffer中得到字节数组
            String strRead = new String(array);
            strRead = String.copyValueOf(strRead.toCharArray(), 0, array.length);
            queue.offer(strRead);
//            String[] hangs = strRead.split("\n");
//            int length = hangs.length;
//            for(int i=0; i< length;i++){
//                String line = hangs[i];
//                if(line.contains("GET")){
//                    try{
//                        String[] splits = line.split("GET");
//                        if(splits[1].contains("HTTP/1.1")){
//                            String[] strings = splits[1].split("HTTP/1.1");
//                            if(strings.length == 2){
//                                String url =strings[0];
//                                createMap(map,url);
//                            }
//                        }
//                    }catch (Exception e){
////                        System.out.println(e.getMessage());
////                        logger.info(e.getMessage());
//                    }
//
//                }else if(line.contains("POST")){
//                    try {
//                        String[] splits = line.split("POST");
//                        String[] strings = splits[1].split(" ");
//                        String url = strings[1];
//                        map = createMap(map, url);
//                    }catch (Exception e){
////                        System.out.println(e.getMessage());
//
////                        logger.info(e.getMessage());
//                    }
//                }
//            }



            byteBuf.clear();
            return bytes;
        }
        return -1;
    }

    public static HashMap<String, Integer> createMap(HashMap<String, Integer> map, String url){
        if(url.contains("?")){
            String[] split = url.split("\\?");
            if(split.length > 2){
                String s = split[0];
                if(map.containsKey(s)){
                    map.put(s,map.get(s)+1);
                }else {
                    map.put(s,1);
                }
            }
        }else{
            if(map.containsKey(url)){
                map.put(url,map.get(url)+1);
            }else {
                map.put(url,1);
            }
        }
        return map;
    }

    //降序排序
    public static  <K, V extends Comparable<? super V>> Map<K, V> sortByComparator(Map<K, V> map) {
        Map<K, V> result = new LinkedHashMap<>();

        map.entrySet().stream()
                .sorted(Map.Entry.<K, V>comparingByValue()
                        .reversed()).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));

        for(Map.Entry<K, V> entry : result.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ",value=" + entry.getValue());
        }
        return result;
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
        TaskPool taskPool = new TaskPool();
        ChannelFileReader reader = new ChannelFileReader("/Users/mac/Desktop/技术大赛/4G.log", 65536);
        Long startTime = System.currentTimeMillis();

        while (reader.read() != -1){


        }

        new Thread(new MyRunnable()).start();

        reader.close();
        sortByComparator(reader.map);



//        BlockingQueue<String> queue = reader.queue;
//        if(!queue.isEmpty()){
//            taskPool.execute(new Task("zzz"));
//        }



        Long endTime = System.currentTimeMillis();
        System.out.printf("stream Diff: %d ms\n", (endTime - startTime));

    }

}
