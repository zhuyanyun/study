package com.example.study.test.read;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class Threadtd extends Thread {

    private List<Future<Map<String, Integer>>> threadMapList;
    public Threadtd(List<Future<Map<String, Integer>>> list){
        threadMapList = list;
    }

    @Override
    public void run(){
        for(Future<Map<String, Integer>> future : threadMapList){

        }
    }
}
