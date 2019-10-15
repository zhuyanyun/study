package com.example.study.test.read;

import java.util.Arrays;

public class test {
    public static void main(String[] args) {

//        String str = "\tinvestor.huihuigj.com    \"GET /share/_nuxt/pages/assistant/com/detail.pages/assistant/com/product-list.pages/assistant/index.02ee9324.js HTTP/1.1\"    200";
//        byte[] ss = str.getBytes();
//
//        int a = 0;
//        int b = 0;
//        for(int y=0; y< ss.length; y++) {
//            System.out.println(ss[y]);
//            System.out.println(ss[y] == 47);
//            if(ss[y] == '/' && a == 0){
//                a = y;
//                byte[] urlByte = Arrays.copyOfRange(ss, a, ss.length);
//                String url = new String(urlByte);
//                System.out.println(url);
//            } else if(ss[y]=='/' && b == 0){
//                b = y;
//                byte[] urlByte = Arrays.copyOfRange(ss, a, b - 4);
//                String url = new String(urlByte);
//                System.out.println(url);
//            }

//        }
        
        
        String str2 = "[26/Sep/2019:00:00:18 +0800]\t-\t47.93.55.252\tservice.zhongyujinkong.com    \"GET /http/task/runIn0Clock.json HTTP/1.1\"    200\t116\t65\t18.556\t18.556\t10.110.63.21:30122\t200\tcurl/7.29.0\t-\tservice.zhongyujinkong.com\t443\ta-wbs-web\troot=";
        byte[] bytes = str2.getBytes();
        int j = 0;
        int k = 0;
        for(int i = 0; i<bytes.length; i++){
            byte aByte = bytes[i];


            if(bytes[i] == 32){
                if(j != 0){
                    k = i;
                }else{
                    j = i;
                }

            }

            System.out.println("j"+j+",k" +k);
            if(j != 0 && k != 0){
                byte[] urlByte = Arrays.copyOfRange(bytes, j, k - j);
                String s = new String(urlByte);
                System.out.println(s);
            }



        }

        byte a = ' ';
        System.out.println("*****"+a);


//        System.out.println(bytes[0]);
//        System.out.println();
//
//
//        System.out.println(bytes);
//        System.out.println(bytes1);
    }
}
