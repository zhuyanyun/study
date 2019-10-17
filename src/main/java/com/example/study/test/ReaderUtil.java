package com.example.study.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReaderUtil {

//    public static HashMap<String, Integer> map = new HashMap<>();


    //降序排序
    public static  <K, V extends Comparable<? super V>> Map<K, V> sortByComparator(Map<K, V> map) {
        Map<K, V> result = new LinkedHashMap<>();

        map.entrySet().stream()
                .sorted(Map.Entry.<K, V>comparingByValue()
                        .reversed()).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));

//        for(Map.Entry<K, V> entry : result.entrySet()) {
//            System.out.println(entry.getKey() + ",value=" + entry.getValue());
//        }

//        try {
//            File file = new File("/");
//            if (!file.getParentFile().exists()) {
//                file.getParentFile().mkdirs();
//            }
//            file.createNewFile();
//            if(result != null && !"".equals(result)){
//                FileWriter fw = new FileWriter(file, true);
//            fw.write(result.toString());//写入本地文件中
//            fw.flush();
//            fw.close();
//            System.out.println("执行完毕!");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }



        return result;
    }

    public static HashMap<String, Integer>  buildMap(String line,HashMap<String,Integer> map){
        if(line.contains("GET")){
            String[] splits = line.split("GET");
            if(splits[1].contains("HTTP/1.1")){
                String[] strings = splits[1].split("HTTP/1.1");
                if(strings.length == 2){
                    String url =strings[0];
                    createMap(map,url);
                }
            }
        }else if(line.contains("POST")){
            String[] splits = line.split("POST");
            String[] strings = splits[1].split(" ");
            String url =strings[1];
            map = createMap(map, url);
        }
        return map;
    }


    //url加入map中
    public static HashMap<String, Integer> createMap(HashMap<String, Integer> map, String url){
        if(url.contains("?")){
            String[] split = url.split("\\?");
            if(split.length > 2){
                String s = split[0];
                if(map.containsKey(s)){
                    map.put(s,map.get(s)+1);
                }else {
                    map.put(s,1);
                }
            }
        }else{
            if(map.containsKey(url)){
                map.put(url,map.get(url)+1);
            }else {
                map.put(url,1);
            }
        }
        return map;
    }
}
