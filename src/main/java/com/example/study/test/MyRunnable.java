package com.example.study.test;

import com.fasterxml.jackson.databind.util.JSONPObject;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class MyRunnable implements Runnable {

    StringBuilder sb = null;
    @Override
    public void run() {
        System.out.println("======分配线程开始：");
        BlockingQueue<String> queue = ChannelFileReader.queue;
        TaskPool taskPool = new TaskPool();
        for(;;){
            ArrayList<String> list = new ArrayList<>();
            if(!queue.isEmpty()){
                int i = 1;
                String poll = queue.poll();
                String[] split = poll.split("\n");
                for(int j=0;j<split.length;j++){
                    if(split[i].contains("root")){
                        list.add(split[j]);
                    }else{
                        if(sb == null){
                            sb.append(split[j]);
                        }else {
                            sb.append(split[i]);
                            list.add(sb.toString());
                            sb = null;
                        }
                    }
                }
                System.out.println(split[0]);
            }
            taskPool.execute(new ComputeRunnable(list));
        }
    }
}
