package org.ultra_low_latency_order_matching_engine;

import org.ultra_low_latency_order_matching_engine.enums.OrderType;
import org.ultra_low_latency_order_matching_engine.model.Order;
import org.ultra_low_latency_order_matching_engine.services.MatchingEngine;
import org.ultra_low_latency_order_matching_engine.services.OrderBook;
import org.ultra_low_latency_order_matching_engine.services.Producer;
import org.ultra_low_latency_order_matching_engine.services.RingBuffer;

import java.util.Arrays;

public class Main {
    private static Producer producer1;
    private static Producer producer2;
    private static Thread producerthread1;
    private static Thread producerthread2;
    private static MatchingEngine engine;
    private static Producer[] producers;
    private static Thread[] threads;

    public static void main(String[] args) throws InterruptedException {

        RingBuffer ringBuffer = new RingBuffer(8192);
        OrderBook orderBook = new OrderBook();
        Thread.sleep(3000); // for pre-warmup
        startMatchingEngine(ringBuffer, orderBook);
        startProducers(ringBuffer, 3);
        long startTime = System.nanoTime();
        Thread.sleep(5000);
        stopProducers();
        while(MatchingEngine.getConsumedCount() < Producer.getProducedCount()){
            Thread.yield();
        }
        stopMatchingQueue();
        ringBuffer.publish(new Order(-1, 1, 1, OrderType.BUY));
        long endTime = System.nanoTime();
        printPerformance(orderBook, startTime, endTime, engine);

        System.out.println("Total produced: " + Producer.getProducedCount());
        System.out.println("Total consumed: " + MatchingEngine.getConsumedCount());
    }

    private static void printPerformance(OrderBook orderBook, long startTime, long endTime, MatchingEngine engine) {
        long duration = endTime - startTime;
        double seconds = duration / 1_000_000_000.0;
        long totalTrades = orderBook.getTradeCount();
        double throughPut = totalTrades / seconds;
        System.out.println("Total trades: " + totalTrades);
        System.out.println("Throughput: " + throughPut + " trades/sec");
        long[] latencies = orderBook.getLatencies();
        if (latencies.length == 0) {
            System.out.println("No latency data");
            return;
        }
        Arrays.sort(latencies, 0, orderBook.getLatencyCount());
        int size = orderBook.getLatencyCount();
        long p50 = latencies[size * 50 / 100];
        long p95 = latencies[size * 95 / 100];
        long p99 = latencies[size * 99 / 100];
        System.out.println("p50 latency (us): " + p50 / 1000);
        System.out.println("p95 latency (us): " + p95 / 1000);
        System.out.println("p99 latency (us): " + p99 / 1000);
        long[] processingLatencies = engine.getProcessingLatencies();
        Arrays.sort(processingLatencies, 0, (int) engine.getTotalProcessedCount());
        size = (int) engine.getTotalProcessedCount();
        p50 = engine.getProcessingLatencies()[size * 50 / 100];
        p95 = engine.getProcessingLatencies()[size * 95 / 100];
        p99 = engine.getProcessingLatencies()[size * 99 / 100];
        System.out.println("p50 latency processing: " + p50);
        System.out.println("p95 latency processing: " + p95);
        System.out.println("p99 latency processing: " + p99);
    }

    private static void stopMatchingQueue() {
        engine.stop();
    }

    private static void stopProducers() throws InterruptedException {
        for(Producer producer: producers){
            producer.stop();
        }
        for(Thread thread: threads){
            thread.join();
        }
    }

    private static void startProducers(RingBuffer ringBuffer, int producerCount) {
        producers = new Producer[producerCount];
        threads = new Thread[producerCount];

        for (int i = 0; i < producerCount; i++) {
            producers[i] = new Producer(ringBuffer);
            threads[i] = new Thread(producers[i]);
            threads[i].start();
        }
    }

    private static void startMatchingEngine(RingBuffer ringBuffer, OrderBook orderBook) {
        engine = new MatchingEngine(ringBuffer, orderBook);
        Thread thread = new Thread(engine);
        thread.start();
    }

}