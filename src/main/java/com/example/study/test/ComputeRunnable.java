package com.example.study.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ComputeRunnable implements Runnable {

    public static ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap<String,Integer>(200);

    private List<String> list;

    public ComputeRunnable(List<String> list){
        list = list;
    }

    //用map统计
    @Override
    public void run() {
        for(String s : list){
            HashMap<String, Integer> stringIntegerHashMap = ReaderUtil.buildMap(s);
        }
    }
}
