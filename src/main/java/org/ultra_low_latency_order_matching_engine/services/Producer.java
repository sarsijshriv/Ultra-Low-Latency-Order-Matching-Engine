package org.ultra_low_latency_order_matching_engine.services;

import org.ultra_low_latency_order_matching_engine.enums.OrderType;
import org.ultra_low_latency_order_matching_engine.model.Order;

import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class Producer implements Runnable {
    private final BlockingQueue<Order> queue;
    private final int producerId;

    private final Random random = new Random();

    private volatile boolean running = true;

    public Producer(BlockingQueue<Order> queue, int producerId) {
        this.queue = queue;
        this.producerId = producerId;
    }

    @Override
    public void run() {
        long orderId = producerId * 1000000L;
        while (running) {
            long price = 9900 + random.nextInt(200);
            long quantity = 1 + random.nextInt(10);
            OrderType orderType = random.nextBoolean() ? OrderType.BUY : OrderType.SEll;
            Order order = new Order(orderId++, price, quantity, orderType);

            try {
                queue.put(order);
                Thread.sleep(0);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void stop() {
        running = false;
    }
}
