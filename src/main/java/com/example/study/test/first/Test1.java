package com.example.study.test.first;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class Test1 {
    static String path = "/Users/mac/Desktop/技术大赛/access_20190926.log";

    public static void main(String[] s) throws IOException {
        stream();
//        mem();
    }

    public static void stream() throws FileNotFoundException, IOException {
        Long startTime = System.currentTimeMillis();
        BufferedReader reader = getReader(new File(path));

        String line;
        HashMap<String, Integer> map = new HashMap<>();
        while ((line = reader.readLine()) != null) {
            // 空转
//            System.out.println(line);
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
        }
        Long estimatedTime = System.currentTimeMillis() - startTime;

        sortByComparator(map);

//        Set set = map2.entrySet();
//
//        for(Map.Entry<String, Integer> entry : map2.entrySet()) {
//            System.out.println("Key = " + entry.getKey() + ",value=" + entry.getValue());
//        }
        System.out.printf("stream Diff: %d ms\n", estimatedTime);
    }

    //降序排序
    public static  <K, V extends Comparable<? super V>> Map<K, V> sortByComparator(Map<K, V> map) {
        Map<K, V> result = new LinkedHashMap<>();

        map.entrySet().stream()
                .sorted(Map.Entry.<K, V>comparingByValue()
                        .reversed()).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));

        for(Map.Entry<K, V> entry : result.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ",value=" + entry.getValue());
        }
        return result;
    }

    public static HashMap<String, Integer> createMap(HashMap<String, Integer> map,String url){
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

    public static BufferedReader getReader(File f) throws FileNotFoundException, IOException {
        BufferedReader reader = null;
        if (f.getName().endsWith(".gz")) {
            reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(f))));
        } else {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
        }
        return reader;
    }

//    public static void mem() throws IOException {
//        Long startTime = System.currentTimeMillis();
//        FileChannel fc = new FileInputStream(path).getChannel();
//        MappedByteBuffer byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
//        //Charset charset = Charset.forName("US-ASCII");
//        Charset charset = Charset.forName("iso-8859-1");
//        CharsetDecoder decoder = charset.newDecoder();
//        CharBuffer charBuffer = decoder.decode(byteBuffer);
//        Scanner sc = new Scanner(charBuffer).useDelimiter(System.getProperty("line.separator"));
//        while (sc.hasNext()) {
//            sc.next();
//        }
//        fc.close();
//        Long estimatedTime = System.currentTimeMillis() - startTime;
//        System.out.printf("mem Diff: %d ms", estimatedTime);
//    }
}  