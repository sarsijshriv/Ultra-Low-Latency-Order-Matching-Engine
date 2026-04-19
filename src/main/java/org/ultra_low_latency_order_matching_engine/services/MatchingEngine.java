package org.ultra_low_latency_order_matching_engine.services;

import org.ultra_low_latency_order_matching_engine.model.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class MatchingEngine implements Runnable {

    private final ArrayBlockingQueue<Order> queue;
    private final OrderBook orderBook;
    private static boolean running = true;
    private final List<Long> processingLatencies = new ArrayList<>();

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
                processingLatencies.add(processingEnd - processingStart);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void stop() {
        running = false;
    }

    public List<Long> getProcessingLatencies() {
        return processingLatencies;
    }

}
