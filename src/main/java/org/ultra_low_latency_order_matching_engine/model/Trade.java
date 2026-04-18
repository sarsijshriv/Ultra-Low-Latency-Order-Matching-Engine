package org.ultra_low_latency_order_matching_engine.model;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Trade {
    private final long buyOrderId;
    private final long sellOrderId;
    private final long price;
    private final long quantity;
    private final long timestamp;

    public Trade(long buyOrderId, long sellOrderId, long price, long quantity, long timestamp) {
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.price = price;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }
}
