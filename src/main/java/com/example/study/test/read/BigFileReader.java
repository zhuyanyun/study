package com.example.study.test.read;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class BigFileReader {
    private int threadSize;
    private String charset;
    private int bufferSize;
    private IHandle handle;
    private ExecutorService  executorService;
    private long fileLength;
    private RandomAccessFile rAccessFile;
    private Set<StartEndPair> startEndPairs;
    private CyclicBarrier cyclicBarrier;
    private AtomicLong counter = new AtomicLong(0);

    private BigFileReader(File file,IHandle handle,String charset,int bufferSize,int threadSize){
        this.fileLength = file.length();
        this.handle = handle;
        this.charset = charset;
        this.bufferSize = bufferSize;
        this.threadSize = threadSize;
        try {
            this.rAccessFile = new RandomAccessFile(file,"r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.executorService = Executors.newFixedThreadPool(threadSize);
        startEndPairs = new HashSet<BigFileReader.StartEndPair>();
    }

    public void start() throws ExecutionException, InterruptedException {
        System.out.println("文件长度：" + fileLength);
        long everySize = this.fileLength/this.threadSize;
        try {
            //处理断行
            calculateStartEnd(0, everySize);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        final long startTime = System.currentTimeMillis();
        cyclicBarrier = new CyclicBarrier(startEndPairs.size(),new Runnable() {

            @Override
            public void run() {
                System.out.println("use time: "+(System.currentTimeMillis()-startTime));
                System.out.println("all line: "+counter.get());
            }
        });

        HashMap threadMap = new HashMap<String, Map>();

        List<Future<Map<String, Map>>> threadMapList = new ArrayList<Future<Map<String, Map>>>();
        for(StartEndPair pair:startEndPairs){
            System.out.println("分配分片："+pair);
            Future<Map<String, Map>> submit = this.executorService.submit(new SliceReaderTask(pair, threadMap));
//            Map<String, Map> stringMapMap = submit.get();
//            System.out.println(submit);
//            threadMapList.add(submit);
        }

        // 获取10个任务的返回结果
//        for ( int i=0; i<7; i++ ) {
//            // 获取包含返回结果的future对象
//            Future<Map<String, Map>> mapFuture = threadMapList.get(i);
//            // 从future中取出执行结果（若尚未返回结果，则get方法被阻塞，直到结果被返回为止）
//            System.out.println(mapFuture);
//            Map<String, Map> stringMapMap = mapFuture.get();
//            System.out.println(stringMapMap);
//        }
    }

    //移动指针到行尾
    private void calculateStartEnd(long start,long size) throws IOException{
        if(start>fileLength-1){
            return;
        }
        StartEndPair pair = new StartEndPair();
        pair.start=start;
        long endPosition = start+size-1;
        if(endPosition>=fileLength-1){
            pair.end=fileLength-1;
            startEndPairs.add(pair);
            return;
        }

        rAccessFile.seek(endPosition);
        byte tmp =(byte) rAccessFile.read();
        while(tmp!='\n' && tmp!='\r'){
            endPosition++;
            if(endPosition>=fileLength-1){
                endPosition=fileLength-1;
                break;
            }
            rAccessFile.seek(endPosition);
            tmp =(byte) rAccessFile.read();
        }
        pair.end=endPosition;
        startEndPairs.add(pair);

        calculateStartEnd(endPosition+1, size);

    }



    public void shutdown(){
        try {
            this.rAccessFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.executorService.shutdown();
    }

    private void handle(byte[] bytes) throws UnsupportedEncodingException{
        String line = null;
        if(this.charset==null){
            line = new String(bytes);
        }else{
            line = new String(bytes,charset);
        }
        if(line!=null && !"".equals(line)){
            this.handle.handle(line);
            counter.incrementAndGet();
        }
    }

    private static class StartEndPair{
        public long start;
        public long end;

        @Override
        public String toString() {
            return "star="+start+";end="+end;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (int) (end ^ (end >>> 32));
            result = prime * result + (int) (start ^ (start >>> 32));
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            StartEndPair other = (StartEndPair) obj;
            if (end != other.end)
                return false;
            if (start != other.start)
                return false;
            return true;
        }

    }
    private class SliceReaderTask implements Callable<Map<String,Map>> {
        private long start;
        private long sliceSize;
        private byte[] readBuff;

        private HashMap threadMap = new HashMap<String, Map>();
        /**
         * @param  	pair position (include)
         * @param map 	the position read to(include)
         */
        public SliceReaderTask(StartEndPair pair,HashMap map) {
            this.start = pair.start;
            this.sliceSize = pair.end-pair.start+1;
//            this.readBuff = new byte[bufferSize];
            this.readBuff = new byte[bufferSize];
            threadMap = map;

        }

        @Override
        public Map<String,Map> call() {
            HashMap<String, Integer> urlMap = new HashMap<>();
            try {
//                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                MappedByteBuffer mapBuffer = rAccessFile.getChannel().map(MapMode.READ_ONLY,start, this.sliceSize);
                int j = 0;
                ArrayList<Byte> byteList = new ArrayList<Byte>();
                HashMap<Integer, List> byteMap = new HashMap<>();

                StringBuilder stringBuilder = new StringBuilder();
                int m = 0;
                Boolean b = false;
                Boolean b2 = false;
                Boolean falg = false;
                for(; ;){
                    //循环每一个字节
                    byte tmp = mapBuffer.get();

                   if(tmp=='\n' || tmp=='\r'){
                        m = 0;
                        b = false;
                        b2 = false;
                        falg = false;
                        continue;
                    }else if(falg){
                        continue;
                    } else{
//                        for(;;){
                        if(tmp == '\t' || b){
                            if(m < 3){
                                m++;
                                if(m==3){
                                    b = true;
                                }
                            } else{
                                if(tmp == '/'){
                                    byteList.add(tmp);
                                    b2 = true;
                                }else if(b2){
                                    if(tmp == 32){
                                        byte[] bytes = new byte[byteList.size()];
                                        for(int i=0;i<byteList.size();i++){
                                            bytes[i] = byteList.get(i);
                                        }
                                        String  ss = new String(bytes);
                                        System.out.println("========="+ss);
                                        falg = true;
                                        continue;
                                    }
                                    byteList.add(tmp);
                                }
                            }
                        }
                    }

                }

//                cyclicBarrier.await();//测试性能用
            }catch (Exception e) {
                e.printStackTrace();
            }
            return threadMap;
        }

    }

    public static class Builder{
        private int threadSize=1;
        private String charset=null;
        private int bufferSize=1024*1024;
        private IHandle handle;
        private File file;
        public Builder(String file,IHandle handle){
            this.file = new File(file);
            if(!this.file.exists())
                throw new IllegalArgumentException("文件不存在！");
            this.handle = handle;
        }

        public Builder withTreahdSize(int size){
            this.threadSize = size;
            return this;
        }

        public Builder withCharset(String charset){
            this.charset= charset;
            return this;
        }

        public Builder withBufferSize(int bufferSize){
            this.bufferSize = bufferSize;
            return this;
        }

        public BigFileReader build(){
            return new BigFileReader(this.file,this.handle,this.charset,this.bufferSize,this.threadSize);
        }
    }


}

