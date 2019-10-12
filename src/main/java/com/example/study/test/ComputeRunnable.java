package com.example.study.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

public class ComputeRunnable implements Callable {

//    public static ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap<String,Integer>(200);
//    public static volatile HashMap map = new HashMap<String,Integer>();

    private List<String> list;

    private String name;

    public ComputeRunnable(List<String> strlist,String nameStr){
        list = strlist;
        name = nameStr;
    }

    //用map统计

    @Override
    public HashMap<String, HashMap> call() throws Exception {
        HashMap<String, HashMap> threadMap = new HashMap<>();
        System.out.println("开始统计线程");
        HashMap map = new HashMap<String,Integer>();

        for(String s : list){
            map = ReaderUtil.buildMap(s,map);
        }
        threadMap.put(name, map);
        return threadMap;
    }
}
