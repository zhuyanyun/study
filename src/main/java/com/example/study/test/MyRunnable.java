package com.example.study.test;

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
            if(!queue.isEmpty()){
                int i = 1;
                String poll = queue.poll();
                String[] split = poll.split("\n");
                ArrayList<String> list = new ArrayList<>();
                for(int j=0;j<split.length;j++){
                    if(split[i].contains("root")){
                        list.add(split[j]);
                    }else{
                        if(sb == null){
                            sb.append(split[j]);
                        }else {

                        }
                    }
                }

                taskPool.execute(new ComputeRunnable(list));

                System.out.println(split[0]);
//                taskPool.execute(new Task("zzz" + i,));
                i++;
            }
        }
    }
}
