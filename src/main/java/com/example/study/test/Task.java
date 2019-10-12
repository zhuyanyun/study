package com.example.study.test;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class Task implements Runnable{

    private String name;

    //处理后的整行数据；
    private String str;

    private Map map;

    public Task(String name,Map map){
        this.name = name;
        this.map = map;
    }

    public void run(){
        // do something
        try{
            System.out.println("我的名字是：" + this.name);
            BlockingQueue<String> queue = ChannelFileReader.queue;
            String poll = queue.poll();
//            ReaderUtil.buildMap(poll,map);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

    }
}
