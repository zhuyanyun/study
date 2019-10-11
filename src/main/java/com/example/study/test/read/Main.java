package com.example.study.test.read;

import com.example.study.test.ReaderUtil;

import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        Long startTime = System.currentTimeMillis();

        BigFileReader.Builder builder = new BigFileReader.Builder("/Users/mac/Desktop/技术大赛/4G.log",new IHandle() {

            @Override
            public void handle(String line) {
//                HashMap<String, Integer> map = ReaderUtil.buildMap(line);
            }

        });
        builder.withTreahdSize(10)
                .withCharset("utf-8")
                .withBufferSize(1024*1024);
        BigFileReader bigFileReader = builder.build();
        bigFileReader.start();

        Long estimatedTime = System.currentTimeMillis() - startTime;
//        System.out.printf("stream Diff: %d ms\n", estimatedTime);

    }

}
