package org.ultra_low_latency_order_matching_engine.services;

import org.ultra_low_latency_order_matching_engine.enums.OrderType;
import org.ultra_low_latency_order_matching_engine.model.Order;

import java.util.Comparator;
import java.util.PriorityQueue;

public class OrderBook {

    private final PriorityQueue<Order> buyOrders;
    private final PriorityQueue<Order> sellOrders;
    private static final int MAX_TRADES = 20_000_000;
    private long tradeCount = 0;
    private final long[] latencies = new long[MAX_TRADES];
    private int latencyIndex = 0;

    public OrderBook() {
        buyOrders = new PriorityQueue<>(
                (order1, order2) -> {
                    int priceCompare = Long.compare(order2.getPrice(), order1.getPrice());
                    if(priceCompare !=0){
                        return priceCompare;
                    }
                    return Long.compare(order1.getId(), order2.getId());
                }
        );
        sellOrders = new PriorityQueue<>(
                (order1, order2) -> {
                    int priceCompare = Long.compare(order1.getPrice(), order2.getPrice());
                    if(priceCompare !=0){
                        return priceCompare;
                    }
                    return Long.compare(order1.getId(), order2.getId());
                }
        );
    }

    public void addOrder(Order order) {
        if (order.getOrderType() == OrderType.BUY) {
            buyOrders.add(order);
        } else {
            sellOrders.add(order);
        }
        matchOrders();
    }

    private void matchOrders() {
        while (true) {
            Order buyOrder = buyOrders.peek();
            Order sellOrder = sellOrders.peek();

            if(buyOrder == null || sellOrder == null) break;

            long buyPrice = buyOrder.getPrice();
            long sellPrice = sellOrder.getPrice();

            if(sellPrice > buyPrice) break;

            long buyQuantity = buyOrder.getQuantity();
            long sellQuantity = sellOrder.getQuantity();

            long tradeQty = buyQuantity < sellQuantity ? buyQuantity : sellQuantity;
            tradeCount++;
            latencies[latencyIndex++] = System.nanoTime() - buyOrder.getCreatedTime();
            buyOrder.reduceQuantity(tradeQty);
            sellOrder.reduceQuantity(tradeQty);
            if (buyOrder.isFilled()) {
                buyOrders.poll();
            }
            if (sellOrder.isFilled()) {
                sellOrders.poll();
            }
        }
    }

    public long[] getLatencies() {
        return latencies;
    }

    public Long getTradeCount() {
        return tradeCount;
    }

    public int getLatencyCount() {
        return latencyIndex;
    }
}
