package org.ultra_low_latency_order_matching_engine.services;

import org.ultra_low_latency_order_matching_engine.enums.OrderType;
import org.ultra_low_latency_order_matching_engine.model.Order;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class Producer implements Runnable {
    private final RingBuffer ringBuffer;
    private volatile boolean running = true;
    private static final AtomicLong producedCount = new AtomicLong(0);
    public Producer(RingBuffer ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    @Override
    public void run() {
        while (running) {
            Order order = generateOrder();
            producedCount.getAndIncrement();
            while (!ringBuffer.publish(order)) {
                Thread.yield();
            }
        }
    }

    private Order generateOrder() {
        Random random = new Random();
        int orderId = random.nextInt(100000);
        long price = 9900 + random.nextInt(200);
        long quantity = 1 + random.nextInt(10);
        OrderType orderType = random.nextBoolean() ? OrderType.BUY : OrderType.SEll;
        return new Order(orderId++, price, quantity, orderType);
    }

    public void stop() {
        running = false;
    }

    public static long getProducedCount(){
        return producedCount.get();
    }
}
