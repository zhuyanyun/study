package com.example.study.test.read;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    public void start(){
        long everySize = this.fileLength/this.threadSize;
        try {
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
        for(StartEndPair pair:startEndPairs){
            System.out.println("分配分片："+pair);
            this.executorService.execute(new SliceReaderTask(pair));
        }
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
    private class SliceReaderTask implements Runnable{
        private long start;
        private long sliceSize;
        private byte[] readBuff;
        /**
         * @param  	read position (include)
         * @param end 	the position read to(include)
         */
        public SliceReaderTask(StartEndPair pair) {
            this.start = pair.start;
            this.sliceSize = pair.end-pair.start+1;
            this.readBuff = new byte[bufferSize];
        }

        @Override
        public void run() {
            try {
                //放在外面大文件可能造成oom
//                MappedByteBuffer mapBuffer = rAccessFile.getChannel().map(MapMode.READ_ONLY,start, this.sliceSize);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                for(int offset=0;offset<sliceSize;offset+=bufferSize){
                    MappedByteBuffer mapBuffer = rAccessFile.getChannel().map(MapMode.READ_ONLY,start, this.sliceSize);
                    int readLength;
                    if(offset+bufferSize<=sliceSize){
                        readLength = bufferSize;
                    }else{
                        readLength = (int) (sliceSize-offset);
                    }
                    mapBuffer.get(readBuff, 0, readLength);
                    HashMap<Object, Object> HashMap = new HashMap<>();
                    byte[] lineBytes;
                    for(int i=0;i<readLength;i++){
                        int j = 0;
                        byte tmp = readBuff[i];
                        if(tmp=='\n' || tmp=='\r'){
                            //先分行，处理字符串
                            lineBytes = Arrays.copyOfRange(readBuff,j,j+i);
                            int k = 0;
                            int x = 0;
                            for (int m=0;m<lineBytes.length;m++){
                                byte blank = lineBytes[m];
                                if(blank == '\t' && k != 3){
                                    byte[] ss = Arrays.copyOfRange(lineBytes,0,m);
                                    String s2=new String(ss);
                                    System.out.println(s2);
                                    k++;
                                    x = m;
                                }else if (blank == '\t' && k == 3){
                                    byte[] ss = Arrays.copyOfRange(lineBytes,x,m);
                                    String s2=new String(ss);
                                    System.out.println(s2);
                                    for(int y=0; y< ss.length; y++) {
                                        System.out.println(ss[y]);
                                        System.out.println(ss[y] == 47);
                                        if(ss[y] == '/'){
                                            byte[] urlByte = Arrays.copyOfRange(ss, y, ss.length);
                                            String url = new String(urlByte);
                                        }
                                    }
                                }
//                                    String s2=new String(ss);
//                                    System.out.println(s2);
                            }
                        }

//                            String s1=new String(lineBytes);
//                            System.out.println(s1);

//                            concurrentHashMap.put(readBuff[j]);
                            
//                            handle(bos.toByteArray());
//                            bos.reset();
                            j = i;
//                        }else{
////                            bos.write(tmp);
//                        }
                    }
                }
//                if(bos.size()>0){
//                    handle(bos.toByteArray());
//                }
                cyclicBarrier.await();//测试性能用
            }catch (Exception e) {
                e.printStackTrace();
            }
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

