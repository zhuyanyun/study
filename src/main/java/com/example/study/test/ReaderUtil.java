package com.example.study.test;

import com.fasterxml.jackson.databind.util.JSONPObject;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReaderUtil {

//    public static HashMap<String, Integer> map = new HashMap<>();

    private static final String FILE_PATH = "/Users/mac/Desktop/技术大赛/结果/result.txt";

    //降序排序
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByComparator(Map<K, V> map) throws IOException {
        Map<K, V> result = new LinkedHashMap<>();

        map.entrySet().stream()
                .sorted(Map.Entry.<K, V>comparingByValue()
                        .reversed()).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));

        int i = 0;
        StringBuffer str = new StringBuffer();
        for(Map.Entry<K, V> entry : result.entrySet()) {
            i++;
            str.append(entry.getKey()+" : "+ entry.getValue());
            if(i>9){
                break;
            }
        }

        FileWriter fw = new FileWriter(FILE_PATH,true); //file是将要储存文件得地址，true/false是再次保存时是否覆盖上一次文件
        /**写数据*/
        fw.write(str.toString());
        /**释放资源,关闭此文件输出流,
         * 并释放与此流有关的所有系统资源*/
        fw.close();

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
