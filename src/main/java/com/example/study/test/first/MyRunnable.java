//package com.example.study.test.first;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//public class MyRunnable implements Runnable {
//
//    private static volatile HashMap map;
//
//    private ExecutorService executor = Executors.newFixedThreadPool(8);
//
//
//    public MyRunnable(HashMap map){
//        this.map = map;
//    }
//
//    StringBuilder sb = null;
//    @Override
//    public void run() {
//        System.out.println("======分配线程开始：");
//        BlockingQueue<String> queue = ChannelFileReader.queue;
//        TaskPool taskPool = new TaskPool();
//        for(;;){
//            ArrayList<String> list = new ArrayList<>();
//            int i = 1;
//            if(!queue.isEmpty()){
//                String poll = queue.poll();
//                String[] split = poll.split("\n");
//                for(int j=0;j<split.length;j++){
//                    if(split[i].contains("root")){
//                        list.add(split[j]);
//                    }else{
//                        if(sb == null){
//                            sb.append(split[j]);
//                        }else {
//                            sb.append(split[i]);
//                            list.add(sb.toString());
//                            sb = null;
//                        }
//                    }
//                }
//                System.out.println(split[0]);
//            }
//
//
//            //使用线程池不能统计结果
//            executor.submit(new ComputeRunnable(list,"kk" + i));
//
//            i++;
//
//
////            Thread thread = new Thread(String.valueOf(new ComputeRunnable(list, "线程" + i)));
////            thread.start();
//
//            //计划改为fork join框架
//
//        }
//    }
//}
