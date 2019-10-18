package com.example.study.test.buffer;

import java.io.FileWriter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CompareUtil {

    public static void sourMap(Map<Bytes, AtomicInteger> map){
        List<Map.Entry<Bytes, AtomicInteger>> list = new ArrayList<Map.Entry<Bytes, AtomicInteger>>();
        list.addAll(map.entrySet());
        Collections.sort(list, new ValueComparator());



        for(int i=0;i< 10;i++){
            Map.Entry<Bytes, AtomicInteger> entryMap = list.get(i);
            System.out.println(new String(entryMap.getKey().getKey()) + " : " + entryMap.getValue());

        }

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

    }


    private static class ValueComparator implements Comparator<Map.Entry<Bytes, AtomicInteger>> {

         @Override
         public int compare(Map.Entry<Bytes, AtomicInteger> entry1, Map.Entry<Bytes, AtomicInteger> entry2) {
            return entry2.getValue().get() - entry1.getValue().get();
         }


    }

}
