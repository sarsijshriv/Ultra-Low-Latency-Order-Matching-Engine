package org.ultra_low_latency_order_matching_engine;

import org.ultra_low_latency_order_matching_engine.enums.OrderType;
import org.ultra_low_latency_order_matching_engine.model.Order;
import org.ultra_low_latency_order_matching_engine.model.Trade;
import org.ultra_low_latency_order_matching_engine.services.OrderBook;

public class Main {
    public static void main(String[] args) {
        OrderBook orderBook = new OrderBook();

        orderBook.addOrder(new Order(1,10000,10, OrderType.BUY));

        orderBook.addOrder(new Order(2,9000,6,OrderType.SEll));

        orderBook.addOrder(new Order(3,9800,7,OrderType.SEll));

        for(Trade trade: orderBook.getTrades()){
            System.out.println(trade);
        }

    }
}