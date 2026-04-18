package org.ultra_low_latency_order_matching_engine;

import org.ultra_low_latency_order_matching_engine.enums.OrderType;
import org.ultra_low_latency_order_matching_engine.model.Order;
import org.ultra_low_latency_order_matching_engine.model.Trade;
import org.ultra_low_latency_order_matching_engine.services.MatchingEngine;
import org.ultra_low_latency_order_matching_engine.services.OrderBook;

import java.util.concurrent.ArrayBlockingQueue;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        ArrayBlockingQueue<Order> queue = new ArrayBlockingQueue<>(1000);
        OrderBook orderBook = new OrderBook();

        startMatchingEngine(queue, orderBook);

        sendTestOrders(queue);

        Thread.sleep(3000);

        printTrades(orderBook);

    }

    private static void startMatchingEngine(ArrayBlockingQueue<Order> queue, OrderBook orderBook) {
        MatchingEngine engine = new MatchingEngine(queue, orderBook);
        Thread thread = new Thread(engine);
        thread.start();
    }

    private static void sendTestOrders(ArrayBlockingQueue<Order> queue) throws InterruptedException {
        queue.put(new Order(1, 10000, 10, OrderType.BUY));
        queue.put(new Order(2, 9900, 5, OrderType.SEll));
        queue.put(new Order(3, 9800, 7, OrderType.SEll));
    }

    public static void printTrades(OrderBook orderBook) {
        for (Trade trade : orderBook.getTrades()) {
            System.out.println(trade);
        }
    }
}