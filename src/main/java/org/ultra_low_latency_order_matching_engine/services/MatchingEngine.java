package org.ultra_low_latency_order_matching_engine.services;

import org.ultra_low_latency_order_matching_engine.model.Order;

import java.util.concurrent.ArrayBlockingQueue;

public class MatchingEngine implements Runnable {

    private final ArrayBlockingQueue<Order> queue;
    private final OrderBook orderBook;
    private static boolean running = true;
    private static final int MAX_TRADES = 20_000_000;
    private final long[] processingLatencies = new long[(int) MAX_TRADES];
    private static int processingIndex = 0;

    public MatchingEngine(ArrayBlockingQueue<Order> queue, OrderBook orderBook) {
        this.queue = queue;
        this.orderBook = orderBook;
    }

    @Override
    public void run() {
        while (running || !queue.isEmpty()) {
            Order order = null;
            try {
                order = queue.take();
                if (order.getId() == -1) break;
                long processingStart = System.nanoTime();
                orderBook.addOrder(order);
                long processingEnd = System.nanoTime();
                    processingLatencies[processingIndex++] = processingEnd - processingStart;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
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

}
