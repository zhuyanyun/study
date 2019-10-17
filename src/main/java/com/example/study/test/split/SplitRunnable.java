package com.example.study.test.split;

import java.io.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class SplitRunnable implements Runnable {

    int byteSize;
    String partFileName;
    File originFile;
    int startPos;
    CyclicBarrier cyclicBarrier;


    public SplitRunnable(int byteSize, int startPos, String partFileName,
                         File originFile,CyclicBarrier cyc) {
        this.startPos = startPos;
        this.byteSize = byteSize;
        this.partFileName = partFileName;
        this.originFile = originFile;
        cyclicBarrier = cyc;
    }

    public void run() {
        RandomAccessFile rFile;
        OutputStream os;
        try {
            rFile = new RandomAccessFile(originFile, "r");
            byte[] b = new byte[byteSize];
            rFile.seek(startPos);// 移动指针到每“段”开头
            int s = rFile.read(b);
            os = new FileOutputStream(partFileName);
            os.write(b, 0, s);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }
}
