package com.example.study.test.split;

import com.example.study.test.ReaderUtil;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang.StringUtils.leftPad;

public class SplitFile {

    private CyclicBarrier cyclicBarrier;


    public List<String> splitBySize(String fileName, int byteSize)throws IOException {
        List<String> parts=new ArrayList<String>();
        File file=new File(fileName);
        int count=(int)Math.ceil(file.length()/(double)byteSize);
        int countLen=(count+"").length();
        ThreadPoolExecutor threadPool=new ThreadPoolExecutor(count,
                count*3,1, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(count*2));

        final long startTime = System.currentTimeMillis();
        cyclicBarrier = new CyclicBarrier(count,new Runnable() {

            @Override
            public void run() {
                System.out.println("use time: "+(System.currentTimeMillis()-startTime));
            }
        });


        for(int i=0;i<count; i++){
            String partFileName=file.getName()+"."
                    +leftPad((i+1)+"",countLen,'0')+".part";
            threadPool.execute(new SplitRunnable(byteSize,i*byteSize,
                    partFileName,file,cyclicBarrier));
            parts.add(partFileName);
        }
        threadPool.shutdown();

        return parts;
    }

    public static void main(String[] args) throws IOException {
        Long startTime = System.currentTimeMillis();
        SplitFile splitFile = new SplitFile();
//        splitFile.splitBySize("/Users/mac/Desktop/技术大赛/10G-file/10g.log",1024*1024*500);
        splitFile.splitBySize("/Users/mac/Desktop/技术大赛/10G-file/10g.log",1024*1024*50);

        Long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.printf("stream Diff: %d ms\n", estimatedTime);
    }
}
