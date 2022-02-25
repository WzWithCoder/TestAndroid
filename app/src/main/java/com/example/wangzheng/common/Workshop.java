package com.example.wangzheng.common;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Create by wangzheng on 2021/6/22
 */
public class Workshop {
    private static BlockingQueue<Integer> blockingQueue = new LinkedBlockingQueue<>(50);

    public static void main(String[] args) {
        Producer producer = new Producer();
        Thread threadProducer = new Thread(producer);
        threadProducer.start();

        Consumer consumer = new Consumer();
        Thread threadConsumer = new Thread(consumer);
        threadConsumer.start();
    }

    static class Producer implements Runnable {
        private int data;

        @Override
        public void run() {
            try {
                while (data <= 10000) {
                    System.out.println("Workshop-Producer:" + data + "-" + blockingQueue.size());
                    blockingQueue.put(data++);
                    Thread.sleep(new Random().nextInt(20));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class Consumer implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    int data = blockingQueue.take();
                    System.out.println("Workshop-Consumer:" + data + "-" + blockingQueue.size());
                    Thread.sleep(new Random().nextInt(20));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
