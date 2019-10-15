package com.example.study.test.read;

import com.example.study.test.ReaderUtil;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Long startTime = System.currentTimeMillis();

        BigFileReader.Builder builder = new BigFileReader.Builder("/Users/mac/Desktop/技术大赛/access_20190926.log",new IHandle() {

            @Override
            public void handle(String line) {
//                HashMap<String, Integer> map = ReaderUtil.buildMap(line);
            }

        });
        builder.withTreahdSize(8)
                .withCharset("utf-8")
                .withBufferSize(1024*1024*100);
        BigFileReader bigFileReader = builder.build();
        bigFileReader.start();

        Long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.printf("stream Diff: %d ms\n", estimatedTime);

    }

}
