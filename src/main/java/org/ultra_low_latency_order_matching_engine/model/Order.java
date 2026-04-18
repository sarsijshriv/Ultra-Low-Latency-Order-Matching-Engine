package org.ultra_low_latency_order_matching_engine.model;

import lombok.Getter;
import lombok.ToString;
import org.ultra_low_latency_order_matching_engine.enums.OrderType;

@Getter
@ToString(exclude = "createdTime")
public class Order {
    private final long id;
    private final long price;
    private long quantity;
    private final OrderType orderType;
    private final long createdTime;

    public Order(long id, long price, long quantity, OrderType orderType) {
        if (price <= 0)
            throw new IllegalArgumentException("Price just be positive");
        if (quantity <= 0)
            throw new IllegalArgumentException("Quantity must be positive");
        this.id = id;
        this.price = price;
        this.quantity = quantity;
        this.orderType = orderType;
        this.createdTime = System.nanoTime();
    }

    public void reduceQuantity(long qty) {
        if (qty <= 0 || qty > this.quantity)
            throw new IllegalArgumentException("Invalid quantity reduction");
        this.quantity -= qty;
    }

    public boolean isFilled() {
        return quantity == 0;
    }

}
