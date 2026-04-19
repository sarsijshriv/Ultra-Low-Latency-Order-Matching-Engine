package org.ultra_low_latency_order_matching_engine;

import org.ultra_low_latency_order_matching_engine.enums.OrderType;
import org.ultra_low_latency_order_matching_engine.model.Order;
import org.ultra_low_latency_order_matching_engine.model.Trade;
import org.ultra_low_latency_order_matching_engine.services.MatchingEngine;
import org.ultra_low_latency_order_matching_engine.services.OrderBook;
import org.ultra_low_latency_order_matching_engine.services.Producer;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class Main {
    private static Producer producer1;
    private static Producer producer2;
    private static Thread producerthread1;
    private static Thread producerthread2;
    private static MatchingEngine engine;

    public static void main(String[] args) throws InterruptedException {

        ArrayBlockingQueue<Order> queue = new ArrayBlockingQueue<>(1000);
        OrderBook orderBook = new OrderBook();
        startMatchingEngine(queue, orderBook);
        startProducers(queue);
        long startTime = System.nanoTime();
        Thread.sleep(10000);
        stopProducers();
        stopMatchingQueue();
        queue.put(new Order(-1, 1, 1, OrderType.BUY));
        long endTime = System.nanoTime();
        printPerformance(orderBook, startTime, endTime, engine);
    }

    private static void printPerformance(OrderBook orderBook, long startTime, long endTime, MatchingEngine engine) {
        long duration = endTime - startTime;
        double seconds = duration / 1_000_000_000.0;
        long totalTrades = orderBook.getTrades().size();
        double throughPut = totalTrades / seconds;
        System.out.println("Total trades: " + totalTrades);
        System.out.println("Throughput: " + throughPut + " trades/sec");
        List<Long> latencies = orderBook.getLatencies();
        if (latencies.isEmpty()) {
            System.out.println("No latency data");
            return;
        }
        Collections.sort(latencies);
        int size = latencies.size();
        long p50 = latencies.get(size * 50 / 100);
        long p95 = latencies.get(size * 95 / 100);
        long p99 = latencies.get(size * 99 / 100);
        System.out.println("p50 latency (us): " + p50 / 1000);
        System.out.println("p95 latency (us): " + p95 / 1000);
        System.out.println("p99 latency (us): " + p99 / 1000);
        List<Long> processingLatencies = engine.getProcessingLatencies();
        Collections.sort(processingLatencies);
        size = engine.getProcessingLatencies().size();
        p50 = engine.getProcessingLatencies().get(size * 50 / 100);
        p95 = engine.getProcessingLatencies().get(size * 95 / 100);
        p99 = engine.getProcessingLatencies().get(size * 99 / 100);
        System.out.println("p50 latency processing: " + p50);
        System.out.println("p95 latency processing: " + p95);
        System.out.println("p99 latency processing: " + p99);
    }

    private static void stopMatchingQueue() {
        engine.stop();
    }

    private static void stopProducers() {
        producer1.stop();
        producer2.stop();
    }

    private static void startProducers(ArrayBlockingQueue<Order> queue) {
        producer1 = new Producer(queue, 1);
        producer2 = new Producer(queue, 2);

        producerthread1 = new Thread(producer1);
        producerthread2 = new Thread(producer2);

        producerthread1.start();
        producerthread2.start();
    }

    private static void startMatchingEngine(ArrayBlockingQueue<Order> queue, OrderBook orderBook) {
        engine = new MatchingEngine(queue, orderBook);
        Thread thread = new Thread(engine);
        thread.start();
    }

    public static void printTrades(OrderBook orderBook) {
        for (Trade trade : orderBook.getTrades()) {
            System.out.println(trade);
        }
    }
}