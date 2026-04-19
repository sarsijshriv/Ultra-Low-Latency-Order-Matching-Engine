package org.ultra_low_latency_order_matching_engine.services;

import org.ultra_low_latency_order_matching_engine.model.Order;

import java.util.concurrent.atomic.AtomicLong;

public class MatchingEngine implements Runnable {

    RingBuffer ringBuffer;
    private final OrderBook orderBook;
    private static boolean running = true;
    private static final int MAX_TRADES = 20_000_000;
    private final long[] processingLatencies = new long[(int) MAX_TRADES];
    private static int processingIndex = 0;
    private static final AtomicLong consumedCount = new AtomicLong(0);
    public MatchingEngine(RingBuffer ringBuffer, OrderBook orderBook) {
        this.ringBuffer = ringBuffer;
        this.orderBook = orderBook;
    }

    @Override
    public void run() {
        while (running) {
            Order order = null;
            while ((order = ringBuffer.consume()) == null) {
                Thread.yield();
            }
            long start = System.nanoTime();
            orderBook.addOrder(order);
            long end = System.nanoTime();
            if(processingIndex < processingLatencies.length){
                processingLatencies[processingIndex++] = end-start;
            }
            consumedCount.getAndIncrement();
        }
    }

    public void stop() {
        running = false;
    }

    public long[] getProcessingLatencies() {
        return processingLatencies;
    }

    public long getTotalProcessedCount() {
        return processingIndex;
    }
    public static long getConsumedCount(){
        return consumedCount.get();
    }

}
