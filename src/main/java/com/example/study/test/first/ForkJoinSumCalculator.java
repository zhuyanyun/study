package com.example.study.test.first;//package com.example.study.test;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Queue;
//import java.util.concurrent.RecursiveTask;
//
//public class ForkJoinSumCalculator extends RecursiveTask<Map> {
//    //要求和的数组
//    private  Map map = new HashMap<String,Integer>();
//
//    private Queue queue;
////    //子任务处理的数组的开始位置。
////    private final int start;
////    //子任务处理的数组的终止位置
////    private final int end;
//    //不可将任务分解为子任务的数组大小
//    public static final long THRESHOLD = 10000;
//
//    //共用构造函数用于创建主任务
//    public ForkJoinSumCalculator(Queue queue) {
//        this.queue = queue;
//    }
//
////    //私有构造方法用于以递归方式为主任务创建子任务
//    private ForkJoinSumCalculator(Map map) {
//        this.map = map;
////        this.start = start;
////        this.end = end;
//    }
//
//    @Override
//    protected Map compute() {
////        //该任务负责求和的部分的大小
////        int length = end - start;
////        if (length <= THRESHOLD) {
////            return computeSequentially(); //如果大小小于或等于阀值，顺序计算结果
////        }
//        //创建一个子任务来为数组的前一半进行求和
//        ForkJoinSumCalculator leftTask = new ForkJoinSumCalculator(numbers, start, start + length / 2);
//        //利用另一个ForkJoinPool线程异步执行新创建的子任务
//        leftTask.fork();
//        //创建一个任务为数组的后一半进行求和
//        ForkJoinSumCalculator rightTask = new ForkJoinSumCalculator(numbers, start + length / 2, end);
//
//        //同步执行第二个子任务，有可能允许进一步递归划分
//        Long rightResult = rightTask.compute();
//        //读取第一个子任务的结果，如果尚未完成就等待
//        Long leftResult = leftTask.join();
//        //合并两个任务的结果
//        return leftResult + rightResult;
//    }
//
//    //在子任务不可再分时计算结果
//    private long computeSequentially() {
//        long sum = 0;
//        for (int i = start; i < end; i++) {
//            sum += numbers[i];
//        }
//        return sum;
//    }
//}
