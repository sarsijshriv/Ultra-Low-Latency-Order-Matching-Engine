package org.ultra_low_latency_order_matching_engine.services;

import lombok.Getter;
import org.ultra_low_latency_order_matching_engine.enums.OrderType;
import org.ultra_low_latency_order_matching_engine.model.Order;
import org.ultra_low_latency_order_matching_engine.model.Trade;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class OrderBook {

    private final PriorityQueue<Order> buyOrders;
    private final PriorityQueue<Order> sellOrders;
    @Getter
    private final List<Trade> trades;

    public OrderBook(){
        buyOrders = new PriorityQueue<>(
                Comparator.comparingLong(Order::getPrice).reversed().thenComparing(Order::getId)
        );
        sellOrders = new PriorityQueue<>(
                Comparator.comparingLong(Order::getPrice).thenComparingLong(Order::getId)
        );

        trades = new ArrayList<>();
    }

    public void addOrder(Order order){
        if(order.getOrderType() == OrderType.BUY){
            buyOrders.add(order);
        } else{
            sellOrders.add(order);
        }
        matchOrders();
    }

    private void matchOrders() {
        while(!buyOrders.isEmpty() && !sellOrders.isEmpty() &&
                sellOrders.peek().getPrice() <= buyOrders.peek().getPrice()){
            Order buyOrder = buyOrders.peek();
            Order sellOrder = sellOrders.peek();

            long tradeQty = Math.min(buyOrder.getQuantity(), sellOrder.getQuantity());

            long tradePrice = sellOrder.getPrice();

            Trade trade = new Trade(buyOrder.getId(),sellOrder.getId(), tradePrice, tradeQty, System.nanoTime());

            trades.add(trade);
            buyOrder.reduceQuantity(tradeQty);
            sellOrder.reduceQuantity(tradeQty);
            if(buyOrder.isFilled()){
                buyOrders.poll();
            }
            if(sellOrder.isFilled()){
                sellOrders.poll();
            }
        }
    }
}
