package com.example.study.url;

public class Test11 {

    public static void main(String[] args) {
        boolean b = false;
        for (;;) {
            System.out.println("aa");
            if (b) {
                break;
            }
        }


        // 分析字节码  while 的比 for 少  一行 goto
        while (b) {
            System.out.println("aa");
        }
    }
}
