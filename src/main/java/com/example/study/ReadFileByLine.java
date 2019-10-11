package com.example.study;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ReadFileByLine {



    private long readByChannel() throws FileNotFoundException, IOException {
        long counts = 0;
        File file = new File("/Users/mac/Desktop/技术大赛/access_20190926.log");
        FileInputStream fis = new FileInputStream(file);
        FileChannel fc = fis.getChannel();
        ByteBuffer bbuf = ByteBuffer.allocate(2048);
        int offset = 0;
        while((offset = fc.read(bbuf)) != -1) {
            counts = counts + offset;
            bbuf.clear();
        }
        fc.close();
        fis.close();
        return counts;
    }

    public static void main(String[] args) throws IOException {
        ReadFileByLine fileByLine = new ReadFileByLine();
        long start = System.nanoTime();
        fileByLine.readByChannel();
        long end = System.nanoTime();
        System.out.println(end - start);
    }


}
