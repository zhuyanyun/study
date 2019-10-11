package com.example.study;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class ChannelFileReader {

	private FileChannel fileChanne;

	private String charset;

	private ByteBuffer byteBuffer;

	private int bufferSize;

	public ChannelFileReader(FileChannel fileChannel, int bufferSize, String charset) {
		this.fileChanne = fileChannel;
		this.charset = charset;
		this.bufferSize = bufferSize;
		// byteBuffer = ByteBuffer.allocate(bufferSize) ;
	}

//	public String readline() throws IOException {
//
//		if (byteBuffer == null) {
//			byteBuffer = ByteBuffer.allocate(bufferSize);
//
//			int len = fileChanne.read(byteBuffer);
//
//			if (len == -1)
//				return null;
//
//			byteBuffer.flip();
//		}
//
//		byte[] bb = new byte[bufferSize];
//
//		int i = 0;
//
//		while (true) {
//
//			while (byteBuffer.hasRemaining()) {
//
//				byte b = byteBuffer.get();
//
//				if ('\r' == b || '\n' == b) {
//
//					if (byteBuffer.hasRemaining()) {
//						byte n = byteBuffer.get();
//
//						if ('\n' != n) {
//							byteBuffer.position(byteBuffer.position() - 1);
//						}
//
//					} else {
//
//						byteBuffer.clear();
//
//						int len = fileChanne.read(byteBuffer);
//
//						byteBuffer.flip();
//
//						if (len != -1) {
//							byte n = byteBuffer.get();
//
//							if ('\n' != n) {
//								byteBuffer.position(byteBuffer.position() - 1);
//							}
//						}
//
//					}
//
//					return new String(bb, 0, i, charset);
//
//				} else {
//
//					if (i >= bb.length) {
//
//						bb = Arrays.copyOf(bb, bb.length + bufferSize + 1);
//					}
//
//					bb[i++] = b;
//				}
//
//			}
//
//			byteBuffer.clear();
//			int len = fileChanne.read(byteBuffer);
//			byteBuffer.flip();
//
//			if (len == -1 && i == 0) {
//				return null;
//			}
//
//		}
//
//	}
//
//	public void close() throws IOException {
//		this.fileChanne.close();
//	}

	public static void main(String[] args) throws IOException {
//		ChannelFileReader reader = new ChannelFileReader("/Users/mac/Desktop/技术大赛/access_20190926.log", 65536);
//		long start = System.nanoTime();
//		while (reader.read() != -1) ;
//		long end = System.nanoTime();
//		reader.close();
//		System.out.println("ChannelFileReader: " + (end - start));


//		FileChannel fileChannel  = new RandomAccessFile("/Users/mac/Desktop/技术大赛/access_20190926.log", "r").getChannel();
//
//		ChannelFileReader fileReader = new ChannelFileReader(fileChannel, 1024, "utf-8") ;
//		long start = System.nanoTime();
//		String line ;
//
//		while(  ( line = fileReader.readline() ) != null ){
////			System.out.println(line );
//
//		}
//		long end = System.nanoTime();
//
//
//		fileReader.close() ;
//		System.out.println("ChannelFileReader: " + (end - start));


//		JDK1.7中引入了新的文件操作类java.nio.file这个包，
//		其中有个Files类它包含了很多有用的方法来操作文件，比如检查文件是否为隐藏文件，
//		或者是检查文件是否为只读文件。开发者还可以使用Files.readAllBytes(Path)方法把整个文件读入内存，
//		此方法返回一个字节数组，还可以把结果传递给String的构造器，以便创建字符串输出。
//		此方法确保了当读入文件的所有字节内容时，无论是否出现IO异常或其它的未检查异常，
//		资源都会关闭。这意味着在读文件到最后的块内容后，无需关闭文件。
//		要注意，此方法不适合读取很大的文件，因为可能存在内存空间不足的问题。
//		开发者还应该明确规定文件的字符编码，以避免任异常或解析错误。


		//如果是文本文件也可以这么读  调用readAllLines 方法
		try {                                //JDK1.8以后可以省略第二个参数，默认是UTF-8编码
			long start = System.nanoTime();

			List<String> lines = Files.readAllLines(Paths.get("/Users/mac/Desktop/技术大赛/access_20190926.log"), StandardCharsets.UTF_8);
			StringBuilder sb = new StringBuilder();
			for (String line : lines) {
				sb.append(line + "\n");//  \r\n  换行符
			}
			String fromFile = sb.toString();
			long end = System.nanoTime();
			System.out.println("ChannelFileReader: " + (end - start));

			System.out.println(fromFile);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
