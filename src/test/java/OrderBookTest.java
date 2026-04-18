import org.junit.Test;
import org.ultra_low_latency_order_matching_engine.enums.OrderType;
import org.ultra_low_latency_order_matching_engine.model.Order;
import org.ultra_low_latency_order_matching_engine.services.OrderBook;

import static org.junit.Assert.assertEquals;

public class OrderBookTest {

    @Test
    public void testNoMatch(){
        OrderBook book = new OrderBook();
        book.addOrder(
                new Order(1,10000, 10, OrderType.BUY)
        );

        book.addOrder(
                new Order(2,10500,5,OrderType.SEll)
        );

        assertEquals(0, book.getTrades().size());
    }

    @Test
    public void testExactMatch(){
        OrderBook book = new OrderBook();
        book.addOrder(
                new Order(1,10000, 5, OrderType.BUY)
        );

        book.addOrder(
                new Order(2,10000,5,OrderType.SEll)
        );

        assertEquals(1, book.getTrades().size());
    }

}
