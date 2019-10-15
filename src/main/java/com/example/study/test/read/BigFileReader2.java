package com.example.study.test.read;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class BigFileReader2 {
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

    private BigFileReader2(File file, IHandle handle, String charset, int bufferSize, int threadSize){
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
        startEndPairs = new HashSet<BigFileReader2.StartEndPair>();
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
         * @param  	read position (include)
         * @param end 	the position read to(include)
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
                //放在外面大文件可能造成oom
//                MappedByteBuffer mapBuffer = rAccessFile.getChannel().map(MapMode.READ_ONLY,start, this.sliceSize);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                for(int offset=0;offset<sliceSize;offset+=bufferSize){
                    MappedByteBuffer mapBuffer = rAccessFile.getChannel().map(MapMode.READ_ONLY,start, this.sliceSize);
                    int readLength;
                    if(bufferSize<=sliceSize){
                        readLength = (int)sliceSize;
                    }else{
                        readLength = bufferSize;
                    }
                    mapBuffer.get(readBuff, 0, readLength);
                    byte[] lineBytes;
                    int j = 0;
                    //循环分片内容
                    for(int i=0;i<readLength;i++){
                        byte tmp = readBuff[i];
                        
                        //分成每一行数据
                        if(tmp=='\n' || tmp=='\r'){
                            //先分行，处理字符串 lineBytes 一行字符流
                            lineBytes = Arrays.copyOfRange(readBuff,j,j+i);
                            int k = 0;
                            int x = 0;
                            //循环每一行数据，截取掉前面的无用数据
                            Boolean flag = true;
                            for (int m=0;m<lineBytes.length && flag ;m++){
                                byte blank = lineBytes[m];
                                if(blank == '\t' && k != 3){
                                    k++;
                                    x = m;
                                }else if (blank == '\t' && k == 3){
                                    byte[] tailByte = Arrays.copyOfRange(lineBytes,x,lineBytes.length);
                                    for(int y=0; y< tailByte.length && flag; y++) {
                                        if(tailByte[y] == '/'){
                                            //去除前缀后的urlByte
                                            byte[] urlByte = Arrays.copyOfRange(tailByte, y, tailByte.length);
                                            if(urlByte.length > 8){
                                                for(int a = 0; a<urlByte.length && flag; a++){
                                                    //这一行已经统计结束，可以跳出行循环；
                                                    if(urlByte[a] == 32){
                                                        byte[] realByte = Arrays.copyOfRange(urlByte, 0, a);
                                                        String url = new String(realByte);
                                                        if(urlMap.containsKey(url)){
                                                            urlMap.put(url,urlMap.get(url)+1);
                                                        }else {
                                                            urlMap.put(url,1);
                                                        }
                                                        flag = false;
                                                        break;
                                                    }
                                                }
                                            }
                                            flag = false;
                                            break;
                                        }
                                    }
//                                    break;
                                }
                            }
                        }
                        j = i;
                    }
//                    break;
//                }
                System.out.println("============");

                for (Map.Entry<String, Integer> m : urlMap.entrySet()) {
                    System.out.println("key:" + m.getKey() + " value:" + m.getValue());
                }
                threadMap.put(Thread.currentThread().getName(),urlMap);
                System.out.println(threadMap);
                if(bos.size()>0){
                    System.out.println("=====");
//                    handle(bos.toByteArray());
                }
                cyclicBarrier.await();//测试性能用
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

        public BigFileReader2 build(){
            return new BigFileReader2(this.file,this.handle,this.charset,this.bufferSize,this.threadSize);
        }
    }


}

