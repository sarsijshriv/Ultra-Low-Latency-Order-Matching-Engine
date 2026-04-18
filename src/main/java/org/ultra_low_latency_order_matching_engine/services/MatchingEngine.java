package org.ultra_low_latency_order_matching_engine.services;

import org.ultra_low_latency_order_matching_engine.model.Order;

import java.util.concurrent.ArrayBlockingQueue;

public class MatchingEngine implements Runnable {

    private final ArrayBlockingQueue<Order> queue;
    private final OrderBook orderBook;

    public MatchingEngine(ArrayBlockingQueue<Order> queue, OrderBook orderBook) {
        this.queue = queue;
        this.orderBook = orderBook;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Order order = queue.take();
                orderBook.addOrder(order);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

}
