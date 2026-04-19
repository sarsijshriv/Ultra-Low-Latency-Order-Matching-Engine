package org.ultra_low_latency_order_matching_engine.services;

import org.ultra_low_latency_order_matching_engine.model.Order;

import java.util.concurrent.atomic.AtomicInteger;

public class RingBuffer {
    private final Order[] buffer;
    private final int capacity;
    private final AtomicInteger writeIndex = new AtomicInteger(0);
    long p1,p2,p3,p4,p5,p6,p7,p8;
    private volatile int readIndex = 0;
    long p9,p10,p11,p12,p13,p14,p15,p16;
    private final int mask;

    public RingBuffer(int capacity) {
        if(Integer.bitCount(capacity)!=1){
            throw new IllegalArgumentException("Capacity must be power of 2");
        }
        this.capacity = capacity;
        this.mask = capacity - 1;
        this.buffer = new Order[capacity];
    }

    public boolean publish(Order order) {
        while (true) {
            int currentWrite = writeIndex.get();
            int nextWrite = (currentWrite + 1) & mask; // equivalent of (currentWrite + 1) % capacity
            int currentRead = readIndex;
            //Buffer full
            if (nextWrite == currentRead) return false;

            if (writeIndex.compareAndSet(currentWrite, nextWrite)) {
                buffer[currentWrite] = order;
                return true;
            }
            Thread.yield();
        }
    }

    public Order consume() {

        int currentRead = readIndex;
        /* buffer empty*/
        if (currentRead == writeIndex.get()) {
            return null;
        }
        Order order = buffer[currentRead];
        readIndex = (currentRead + 1) & mask; // equivalent of (currentWrite + 1) % capacity
        return order;
    }
}
