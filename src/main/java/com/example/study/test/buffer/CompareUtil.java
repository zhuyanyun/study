package com.example.study.test.buffer;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CompareUtil {

    private static final String FILE_PATH = "/Users/mac/Desktop/技术大赛/结果/朱延云.csv";
    private static File file = new File(FILE_PATH);
    private static final String LINE = "\n";
    private static final String DOMAIN = "域名";
    private static final String URL = "URL";



    public static void sourMap(Map<Bytes, AtomicInteger> map,Map<Bytes, AtomicInteger> apiMap) throws IOException {
        List<Map.Entry<Bytes, AtomicInteger>> list = sort(map);
        List<Map.Entry<Bytes, AtomicInteger>> entryList = sort(apiMap);
        FileOutputStream fos = new FileOutputStream(file, true);
        OutputStreamWriter osw = new OutputStreamWriter(fos, "GBK");

        outPutFile(osw,list,DOMAIN);
        outPutFile(osw,entryList,URL);

        osw.close();

    }


    public static void outPutFile(OutputStreamWriter osw,List<Map.Entry<Bytes, AtomicInteger>> list,String str) throws IOException {

        for (int i=0;i< 10;i++) {

            if (i == 0) {
                osw.write(str +",访问量,");
                osw.write(LINE);  //换行
            }

            Map.Entry<Bytes, AtomicInteger> entryMap = list.get(i);
            osw.write(new String(entryMap.getKey().getKey()) + "," + entryMap.getValue() + ","); //写入内容
            osw.write(LINE);  //换行

        }
    }

//        for(int i=0;i< 10;i++){
//            Map.Entry<Bytes, AtomicInteger> entryMap = list.get(i);
//            System.out.println(new String(entryMap.getKey().getKey()) + " : " + entryMap.getValue());
//        }

//        int i = 0;
//        StringBuffer str = new StringBuffer();
//        for(Map.Entry<K, V> entry : result.entrySet()) {
//            i++;
//            str.append(entry.getKey()+" : "+ entry.getValue());
//            if(i>9){
//                break;
//            }
//        }
//
//        FileWriter fw = new FileWriter(FILE_PATH,true); //file是将要储存文件得地址，true/false是再次保存时是否覆盖上一次文件
//        /**写数据*/
//        fw.write(str.toString());
//        /**释放资源,关闭此文件输出流,
//         * 并释放与此流有关的所有系统资源*/
//        fw.close();

//    }

    public static List<Map.Entry<Bytes, AtomicInteger>>  sort(Map<Bytes, AtomicInteger> map){
        List<Map.Entry<Bytes, AtomicInteger>> list = new ArrayList<Map.Entry<Bytes, AtomicInteger>>();
        list.addAll(map.entrySet());
        Collections.sort(list, new ValueComparator());
        return list;
    }



    private static class ValueComparator implements Comparator<Map.Entry<Bytes, AtomicInteger>> {

         @Override
         public int compare(Map.Entry<Bytes, AtomicInteger> entry1, Map.Entry<Bytes, AtomicInteger> entry2) {
            return entry2.getValue().get() - entry1.getValue().get();
         }


    }

}
