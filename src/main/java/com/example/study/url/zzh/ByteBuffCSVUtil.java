package com.example.study.url.zzh;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * CSV文件导出工具类
 */

public class ByteBuffCSVUtil {

    /**
     * CSV文件生成方法
     */
    public static File createCSVFile(Map<ByteBuffKey, AtomicInteger> head, List<Map.Entry<ByteBuffKey, AtomicInteger>> dataList, String outPutPath,
            String filename, int topNum) {

        File csvFile = null;
        BufferedWriter csvWtriter = null;
        try {
            csvFile = new File(outPutPath + File.separator + filename + ".csv");
            File parent = csvFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            csvFile.createNewFile();

            // GB2312使正确读取分隔符","
            csvWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "GB2312"), 1024);

            // 写入文件内容
            writeRow(dataList, csvWtriter, topNum);
            csvWtriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (csvWtriter != null) {
                    csvWtriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return csvFile;
    }

    /**
     * 写一行数据方法
     */
    private static void writeRow(List<Map.Entry<ByteBuffKey, AtomicInteger>> row, BufferedWriter csvWriter, int topNum) throws IOException {
        if (topNum < 0) {
            topNum = row.size();
        }
        for (int i = 0; i < topNum; i++) {
            Map.Entry<ByteBuffKey, AtomicInteger> item = row.get(i);
            StringBuffer sb = new StringBuffer();
            ByteBuffKey key = item.getKey();
            String rowStr = sb.append("\"").append(new String(key.getKey(), key.getOffset(), key.getLen())).append("\",").append(item.getValue()).toString();
            csvWriter.write(rowStr);
            csvWriter.newLine();
        }
    }

}
